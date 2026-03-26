package org.example;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.entidades.Arvore;
import org.example.entidades.Folha;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisualizadorArvore extends Application {
    private Arvore arvore = new Arvore();
    private Pane canvasArvore;
    private ScrollPane scrollPane;
    private TextField campoValor;
    private double escalaAtual = 1.0;

    private final String COR_VERDE = "#10B981";
    private final String COR_VERMELHO = "#F43F5E";
    private final String COR_AZUL = "#3B82F6";
    private final String COR_FUNDO = "#F8FAFC";
    private final String COR_TEXTO = "#1E293B";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        HBox painelControle = criarPainelControle();
        root.setTop(painelControle);

        canvasArvore = new Pane();
        Group grupoZoom = new Group(canvasArvore);

        scrollPane = new ScrollPane(grupoZoom);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + COR_FUNDO + ";");
        scrollPane.setFitToWidth(true);

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                event.consume();
                if (event.getDeltaY() > 0) {
                    aplicarZoom(1.1);
                } else {
                    aplicarZoom(0.9);
                }
            }
        });

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 900, 700);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> desenharArvore());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> desenharArvore());

        primaryStage.setTitle("Arvore Binaria");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox criarPainelControle() {
        HBox hbox = new HBox(15);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Label label = new Label("Valor:");
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setTextFill(Color.web(COR_TEXTO));

        campoValor = new TextField();
        campoValor.setPromptText("Ex: 50");
        campoValor.setPrefWidth(80);
        campoValor.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #E2E8F0; -fx-padding: 8;");

        campoValor.setOnAction(e -> acaoInserir());

        Button btnInserir = new Button("Inserir");
        estilizarBotao(btnInserir, COR_VERDE, 12);
        btnInserir.setOnAction(e -> acaoInserir());

        Button btnSalvar = new Button("Salvar");
        estilizarBotao(btnSalvar, COR_VERDE, 12);
        btnSalvar.setOnAction(e -> salvarArvoreEmTxt());

        Button btnLimpar = new Button("Limpar");
        estilizarBotao(btnLimpar, COR_VERMELHO, 12);

        btnLimpar.setOnAction(e -> {
            arvore = new Arvore();
            escalaAtual = 1.0;
            canvasArvore.setScaleX(1.0);
            canvasArvore.setScaleY(1.0);
            desenharArvore();
        });

        Button btnZoomIn = new Button("Zoom +");
        estilizarBotao(btnZoomIn, COR_AZUL, 12);
        btnZoomIn.setOnAction(e -> aplicarZoom(1.2));

        Button btnZoomOut = new Button("Zoom -");
        estilizarBotao(btnZoomOut, COR_AZUL, 12);
        btnZoomOut.setOnAction(e -> aplicarZoom(0.8));

        hbox.getChildren().addAll(label, campoValor, btnInserir, btnSalvar, btnLimpar, btnZoomIn, btnZoomOut);
        return hbox;
    }

    private void aplicarZoom(double fator) {
        escalaAtual *= fator;
        canvasArvore.setScaleX(escalaAtual);
        canvasArvore.setScaleY(escalaAtual);
    }

    private void estilizarBotao(Button btn, String corHex, int radius) {
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setTextFill(Color.WHITE);
        btn.setStyle(
                "-fx-background-color: " + corHex + ";" +
                        "-fx-background-radius: " + radius + ";" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-font-weight: bold;"
        );

        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
    }

    private void acaoInserir() {
        try {
            int val = Integer.parseInt(campoValor.getText());
            arvore.inserir(new Folha(val));
            desenharArvore();
            campoValor.clear();
            campoValor.requestFocus();
        } catch (NumberFormatException ex) {
            mostrarErro("Digite um número válido!");
        }
    }

    private int obterProfundidade(Arvore no) {
        if (no == null || no.isEmpty()) return 0;
        int esq = obterProfundidade(no.getEsquerda());
        int dir = obterProfundidade(no.getDireita());
        return Math.max(esq, dir) + 1;
    }

    private void desenharArvore() {
        canvasArvore.getChildren().clear();
        if (arvore.isEmpty()) return;

        int profundidade = obterProfundidade(arvore);

        double larguraViewport = scrollPane.getViewportBounds().getWidth();
        if (larguraViewport <= 0) larguraViewport = 900;


        double margem = 80.0;
        double larguraCalculada = Math.pow(2, profundidade) * 25.0;
        double larguraEfetiva = Math.min(larguraViewport - margem, larguraCalculada);


        double gapInicial = larguraEfetiva / 4.0;


        double larguraFinal = larguraViewport;
        double alturaFinal = Math.max(profundidade * 50 + 100, scrollPane.getViewportBounds().getHeight());

        canvasArvore.setPrefSize(larguraFinal, alturaFinal);


        exibirNo(arvore, larguraFinal / 2, 40, gapInicial);
    }

    private void exibirNo(Arvore node, double x, double y, double gap) {
        if (node.getEsquerda() != null) {
            Line linha = new Line(x, y, x - gap, y + 50);
            linha.setStroke(Color.web("#CBD5E1"));
            linha.setStrokeWidth(2);
            canvasArvore.getChildren().add(linha);
            exibirNo(node.getEsquerda(), x - gap, y + 50, gap / 2);
        }

        if (node.getDireita() != null) {
            Line linha = new Line(x, y, x + gap, y + 50);
            linha.setStroke(Color.web("#CBD5E1"));
            linha.setStrokeWidth(2);
            canvasArvore.getChildren().add(linha);
            exibirNo(node.getDireita(), x + gap, y + 50, gap / 2);
        }


        Circle circulo = new Circle(x, y, 18);
        circulo.setFill(Color.WHITE);
        circulo.setStroke(Color.web(COR_VERDE));
        circulo.setStrokeWidth(3);
        circulo.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);");

        Text texto = new Text(String.valueOf(node.getFolha().getValor()));
        texto.setFont(Font.font("System", FontWeight.BOLD, 12));
        texto.setFill(Color.web(COR_TEXTO));

        double textWidth = texto.getLayoutBounds().getWidth();
        texto.setX(x - (textWidth / 2));
        texto.setY(y + 4);

        canvasArvore.getChildren().addAll(circulo, texto);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), circulo);
        st.setFromX(0); st.setFromY(0); st.setToX(1); st.setToY(1);
        st.play();
    }

    private void salvarArvoreEmTxt() {
        if (arvore.isEmpty()) {
            mostrarErro("A árvore está vazia! Insira valores antes de salvar.");
            return;
        }

        try {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nomeArquivo = "arvore_relatorio_" + dataHora + ".txt";
            File arquivo = new File(nomeArquivo);

            try (FileWriter writer = new FileWriter(arquivo)) {

                writer.write("          ÁRVORE BINÁRIA\n");
                writer.write("================================================\n\n");

                writer.write("Representação (Parênteses Aninhados):\n");
                writer.write(getParentesesAninhados(arvore) + "\n\n");

                writer.write("Percursos:\n");
                writer.write("Pré-ordem: " + getPreOrdem(arvore).trim() + "\n");
                writer.write("Em-ordem:  " + getEmOrdem(arvore).trim() + "\n");
                writer.write("Pós-ordem: " + getPosOrdem(arvore).trim() + "\n\n");

                writer.write("Caminhos (Da Raiz até as Folhas):\n");
                List<String> caminhos = new ArrayList<>();
                getCaminhos(arvore, "", caminhos);
                for (String c : caminhos) {
                    writer.write(c + "\n");
                }
                writer.write("\n");

                writer.write("Tipos de Árvore:\n");
                int totalNos = contarNos(arvore);
                int altura = getAltura(arvore);
                writer.write("Estritamente Binária: " + (isEstritamenteBinaria(arvore) ? "Sim" : "Não") + "\n");
                writer.write("Completa: " + (isCompleta(arvore, 0, totalNos) ? "Sim" : "Não") + "\n");
                writer.write("Cheia/Perfeita: " + (totalNos == (Math.pow(2, altura + 1) - 1) ? "Sim" : "Não") + "\n\n");

                writer.write("Métricas da Árvore Inteira:\n");
                writer.write("Nível da Árvore: " + altura + "\n");
                writer.write("Profundidade da Árvore: " + altura + "\n");
                writer.write("Altura da Árvore: " + altura + "\n\n");

                writer.write("Métricas por Nó (Valor | Nível | Profundidade | Altura):\n");
                List<String> metricas = new ArrayList<>();
                coletarMetricasNos(arvore, 0, metricas);
                for (String m : metricas) {
                    writer.write(m + "\n");
                }
                writer.write("\n================================================\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Árvore Salva");
            alert.setHeaderText("Relatório salvo com sucesso!");
            alert.setContentText("Arquivo gerado: " + arquivo.getAbsolutePath());
            alert.showAndWait();

        } catch (IOException ex) {
            mostrarErro("Erro ao salvar o arquivo TXT: " + ex.getMessage());
        }
    }

    private String getParentesesAninhados(Arvore node) {
        if (node == null || node.isEmpty()) return "";
        String result = String.valueOf(node.getFolha().getValor());
        if (node.getEsquerda() != null || node.getDireita() != null) {
            result += "(";
            result += (node.getEsquerda() != null) ? getParentesesAninhados(node.getEsquerda()) : "-";
            result += ", ";
            result += (node.getDireita() != null) ? getParentesesAninhados(node.getDireita()) : "-";
            result += ")";
        }
        return result;
    }

    private String getPreOrdem(Arvore node) {
        if (node == null || node.isEmpty()) return "";
        return node.getFolha().getValor() + " " +
                (node.getEsquerda() != null ? getPreOrdem(node.getEsquerda()) : "") +
                (node.getDireita() != null ? getPreOrdem(node.getDireita()) : "");
    }

    private String getEmOrdem(Arvore node) {
        if (node == null || node.isEmpty()) return "";
        return (node.getEsquerda() != null ? getEmOrdem(node.getEsquerda()) : "") +
                node.getFolha().getValor() + " " +
                (node.getDireita() != null ? getEmOrdem(node.getDireita()) : "");
    }

    private String getPosOrdem(Arvore node) {
        if (node == null || node.isEmpty()) return "";
        return (node.getEsquerda() != null ? getPosOrdem(node.getEsquerda()) : "") +
                (node.getDireita() != null ? getPosOrdem(node.getDireita()) : "") +
                node.getFolha().getValor() + " ";
    }

    private void getCaminhos(Arvore node, String caminhoAtual, List<String> caminhos) {
        if (node == null || node.isEmpty()) return;
        caminhoAtual += node.getFolha().getValor();
        if (node.getEsquerda() == null && node.getDireita() == null) {
            caminhos.add(caminhoAtual);
        } else {
            caminhoAtual += " -> ";
            if (node.getEsquerda() != null) getCaminhos(node.getEsquerda(), caminhoAtual, caminhos);
            if (node.getDireita() != null) getCaminhos(node.getDireita(), caminhoAtual, caminhos);
        }
    }

    private int contarNos(Arvore node) {
        if (node == null || node.isEmpty()) return 0;
        int esq = node.getEsquerda() != null ? contarNos(node.getEsquerda()) : 0;
        int dir = node.getDireita() != null ? contarNos(node.getDireita()) : 0;
        return 1 + esq + dir;
    }

    private int getAltura(Arvore node) {
        if (node == null || node.isEmpty()) return -1;
        int esq = node.getEsquerda() != null ? getAltura(node.getEsquerda()) : -1;
        int dir = node.getDireita() != null ? getAltura(node.getDireita()) : -1;
        return Math.max(esq, dir) + 1;
    }

    private boolean isEstritamenteBinaria(Arvore node) {
        if (node == null || node.isEmpty()) return true;
        boolean temEsq = node.getEsquerda() != null;
        boolean temDir = node.getDireita() != null;
        if (temEsq ^ temDir) return false;
        boolean esqValida = !temEsq || isEstritamenteBinaria(node.getEsquerda());
        boolean dirValida = !temDir || isEstritamenteBinaria(node.getDireita());
        return esqValida && dirValida;
    }

    private boolean isCompleta(Arvore node, int index, int countNos) {
        if (node == null || node.isEmpty()) return true;
        if (index >= countNos) return false;
        boolean esqCompleta = node.getEsquerda() == null || isCompleta(node.getEsquerda(), 2 * index + 1, countNos);
        boolean dirCompleta = node.getDireita() == null || isCompleta(node.getDireita(), 2 * index + 2, countNos);
        return esqCompleta && dirCompleta;
    }

    private void coletarMetricasNos(Arvore node, int profundidade, List<String> metricas) {
        if (node == null || node.isEmpty()) return;
        int alturaNo = getAltura(node);
        metricas.add(String.format("Nó %3d | Nível: %2d | Profundidade: %2d | Altura: %2d",
                node.getFolha().getValor(), profundidade, profundidade, alturaNo));

        if (node.getEsquerda() != null) coletarMetricasNos(node.getEsquerda(), profundidade + 1, metricas);
        if (node.getDireita() != null) coletarMetricasNos(node.getDireita(), profundidade + 1, metricas);
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}