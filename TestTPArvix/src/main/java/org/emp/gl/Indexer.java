package org.emp.gl;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Indexer {
    private final Map<String, Map<String, Integer>> index = new HashMap<>();

    public void buildIndex(String processedDir, String indexFile) {
        try {
            Files.list(Paths.get(processedDir)).forEach(file -> {
                try {
                    String content = Files.readString(file);
                    String paperId = file.getFileName().toString().replace(".txt", "");
                    indexFileContent(content, paperId);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la lecture du fichier prétraité : " + e.getMessage());
                }
            });

            saveIndexToFile(indexFile);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'indexation : " + e.getMessage());
        }
    }

    private void indexFileContent(String content, String paperId) {
        String[] terms = content.split("\\s+");
        for (String term : terms) {
            index.putIfAbsent(term, new HashMap<>());
            Map<String, Integer> postings = index.get(term);
            postings.put(paperId, postings.getOrDefault(paperId, 0) + 1);
        }
    }

    private void saveIndexToFile(String indexFile) {
        try {
            Path indexDir = Paths.get(indexFile).getParent();
            if (indexDir != null && !Files.exists(indexDir)) {
                Files.createDirectories(indexDir);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile))) {
                oos.writeObject(index);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de l'index : " + e.getMessage());
        }
    }

    public Map<String, Integer> search(String term, String indexFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile))) {
            Map<String, Map<String, Integer>> index = (Map<String, Map<String, Integer>>) ois.readObject();
            return index.getOrDefault(term.toLowerCase(), new HashMap<>());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors de la lecture de l'index : " + e.getMessage());
            return new HashMap<>();
        }
    }
}
