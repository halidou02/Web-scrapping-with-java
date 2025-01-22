package org.emp.gl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ArxivPaper {
    private int paperId;
    private String paperURL;
    private String onlinePDFLink;
    private String localPDFpath;
    private String title;
    private String authors;
    private String abstractText;
    private String paperContent;

    public int getPaperId() { return paperId; }
    public void setPaperId(int paperId) { this.paperId = paperId; }

    public String getPaperURL() { return paperURL; }
    public void setPaperURL(String paperURL) { this.paperURL = paperURL; }

    public String getOnlinePDFLink() { return onlinePDFLink; }
    public void setOnlinePDFLink(String onlinePDFLink) { this.onlinePDFLink = onlinePDFLink; }

    public String getLocalPDFpath() { return localPDFpath; }
    public void setLocalPDFpath(String localPDFpath) { this.localPDFpath = localPDFpath; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getPaperContent() { return paperContent; }
    public void setPaperContent(String paperContent) { this.paperContent = paperContent; }

    public void downloadPDF(String localPath) {
        try {
            // Vérifier que le dossier parent existe, sinon le créer
            java.nio.file.Path parentDir = Paths.get(localPath).getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Télécharger le fichier depuis l'URL
            try (InputStream in = new URL(this.onlinePDFLink).openStream()) {
                Files.copy(in, Paths.get(localPath), StandardCopyOption.REPLACE_EXISTING);
                this.localPDFpath = localPath;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du téléchargement du fichier PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void extractContentFromPDF() {
        if (this.localPDFpath == null) return;

        try (InputStream input = new FileInputStream(new File(this.localPDFpath))) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(input, handler, metadata, new ParseContext());
            this.paperContent = handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
