package org.emp.gl;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Preprocessor {
    private static final Set<String> stopWords = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with"
    ));

    /**
     * Prétraite les fichiers PDF dans un répertoire et sauvegarde les fichiers prétraités.
     *
     * @param pdfDir       Répertoire contenant les fichiers PDF.
     * @param processedDir Répertoire où les fichiers prétraités seront sauvegardés.
     */
    public static void processPDFs(String pdfDir, String processedDir) {
        try {
            Files.createDirectories(Paths.get(processedDir));
            Files.list(Paths.get(pdfDir))
                    .filter(file -> file.toString().endsWith(".pdf"))
                    .forEach(pdfFile -> {
                        try {
                            // Extraction du contenu avec PdfParse
                            String content = PdfParse.extractText(pdfFile.toString());

                            // Prétraitement du contenu
                            String processedContent = preprocess(content);

                            // Sauvegarder le contenu prétraité
                            String processedPath = processedDir + "/" + pdfFile.getFileName().toString().replace(".pdf", ".txt");
                            savePreprocessedFile(processedContent, processedPath);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du traitement du fichier : " + pdfFile + " - " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Erreur lors du parcours du répertoire PDF : " + e.getMessage());
        }
    }

    /**
     * Applique des étapes de prétraitement sur le texte extrait.
     *
     * @param content Texte brut extrait.
     * @return Texte prétraité.
     */
    public static String preprocess(String content) {
        content = content.toLowerCase();
        String[] tokens = content.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        List<String> filteredTokens = Arrays.stream(tokens)
                .filter(token -> !stopWords.contains(token))
                .collect(Collectors.toList());

        List<String> stemmedTokens = filteredTokens.stream()
                .map(Preprocessor::stem)
                .collect(Collectors.toList());

        return String.join(" ", stemmedTokens);
    }

    /**
     * Applique un stemming simplifié.
     *
     * @param word Mot à traiter.
     * @return Mot après stemming.
     */
    private static String stem(String word) {
        if (word.length() <= 3) return word;
        if (word.endsWith("ing") || word.endsWith("ed")) return word.substring(0, word.length() - 3);
        return word;
    }

    /**
     * Sauvegarde le texte prétraité dans un fichier.
     *
     * @param content Texte à sauvegarder.
     * @param filePath Chemin du fichier.
     */
    public static void savePreprocessedFile(String content, String filePath) {
        try {
            Path parentDir = Paths.get(filePath).getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du fichier prétraité : " + e.getMessage());
        }
    }
}
