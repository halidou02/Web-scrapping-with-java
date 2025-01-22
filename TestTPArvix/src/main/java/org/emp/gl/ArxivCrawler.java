package org.emp.gl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ArxivCrawler {
    public List<ArxivPaper> extractAllPapersData(String url) {
        List<ArxivPaper> listOfArxivPapers = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements articles = doc.select("dl#articles > dt"); // Sélection des articles

            for (Element article : articles) {
                ArxivPaper paper = new ArxivPaper();

                // Récupérer l'élément de lien de l'article
                Element linkElement = article.selectFirst("a[href]");
                if (linkElement == null) {
                    System.err.println("Lien introuvable pour un article.");
                    continue;
                }

                // Extraire l'URL de l'article et son ID
                String paperURL = "https://arxiv.org" + linkElement.attr("href");
                String paperId = paperURL.substring(paperURL.lastIndexOf("/") + 1); // Extraire l'ID depuis l'URL
                if (paperId.isEmpty()) {
                    System.err.println("ID de l'article introuvable.");
                    continue;
                }

                // Construire le lien PDF
                String pdfLink = "https://arxiv.org/pdf/" + paperId + ".pdf";

                // Récupérer les détails de l'article
                Element details = article.nextElementSibling(); // <dd> associé
                if (details == null) {
                    System.err.println("Détails introuvables pour l'article : " + paperId);
                    continue;
                }

                String title = details.selectFirst(".list-title") != null
                        ? details.selectFirst(".list-title").text().replace("Title:", "").trim()
                        : "Titre non trouvé";
                String authors = details.select(".list-authors") != null
                        ? details.select(".list-authors").text()
                        : "Auteurs non trouvés";
                String abstractText = details.select("p.mathjax") != null
                        ? details.select("p.mathjax").text()
                        : "Résumé non trouvé";

                // Remplir l'objet paper
                paper.setPaperId(Integer.parseInt(paperId.replaceAll("\\D", ""))); // ID numérique
                paper.setPaperURL(paperURL);
                paper.setOnlinePDFLink(pdfLink);
                paper.setTitle(title);
                paper.setAuthors(authors);
                paper.setAbstractText(abstractText);
                paper.setLocalPDFpath(null);
                paper.setPaperContent(null);

                // Ajouter à la liste
                listOfArxivPapers.add(paper);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction des données : " + e.getMessage());
            e.printStackTrace();
        }

        if (listOfArxivPapers.isEmpty()) {
            System.err.println("Aucun article extrait. Vérifiez l'URL ou les sélecteurs Jsoup.");
        }
        return listOfArxivPapers;
    }

}
