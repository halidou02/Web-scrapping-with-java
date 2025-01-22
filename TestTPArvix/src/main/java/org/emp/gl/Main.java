package org.emp.gl;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Étape 1 : Récupération des données
        ArxivCrawler crawler = new ArxivCrawler();
        List<ArxivPaper> papers = crawler.extractAllPapersData("https://arxiv.org/list/cs/new");

        if (papers.isEmpty()) {
            System.err.println("Aucun article extrait. Vérifiez l'URL ou les sélecteurs Jsoup.");
            return;
        }

        // Étape 2 : Téléchargement et extraction
        for (int i = 0; i < Math.min(5, papers.size()); i++) {
            ArxivPaper paper = papers.get(i);
            System.out.println("Téléchargement du PDF pour : " + paper.getTitle());
            String localPath = "pdfs/" + paper.getPaperId() + ".pdf";
            paper.downloadPDF(localPath);

            if (paper.getLocalPDFpath() == null) {
                System.err.println("Le PDF n'a pas été téléchargé correctement pour : " + paper.getTitle());
                continue;
            }
            paper.extractContentFromPDF();
        }

        String pdfDir = "pdfs";
        // Étape 3 : Prétraitement des fichiers PDF
        String processedDir = "processed_files";
        System.out.println("Prétraitement des fichiers PDF...");
        Preprocessor.processPDFs(pdfDir, processedDir);

     //   Étape 4 : Indexation des fichiers prétraités
        String indexFile = "index/index.dat";
        Indexer indexer = new Indexer();
        indexer.buildIndex(processedDir, indexFile);

        // Étape 5 : Recherche dans l'index
        System.out.println("Recherche dans l'index...");
        String query = "scams"; // Exemple de requête
        Map<String, Integer> results = indexer.search(query, indexFile);
        System.out.println("Résultats pour '" + query + "': " + results);

        }


}
