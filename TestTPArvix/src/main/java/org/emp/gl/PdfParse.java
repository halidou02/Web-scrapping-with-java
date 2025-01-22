package org.emp.gl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class PdfParse {
    /**
     * Méthode pour extraire le contenu textuel d'un fichier PDF.
     *
     * @param filePath Chemin du fichier PDF.
     * @return Contenu textuel extrait du fichier PDF.
     */
    public static String extractText(String filePath) {
        try (InputStream input = new FileInputStream(new File(filePath))) {
            // Gestionnaire de contenu pour stocker le texte extrait
            BodyContentHandler handler = new BodyContentHandler(-1); // Texte illimité
            Metadata metadata = new Metadata();

            // Parser automatique
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            // Extraire le contenu
            parser.parse(input, handler, metadata, context);

            // Retourner le contenu textuel extrait
            return handler.toString();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction du texte du fichier PDF : " + e.getMessage());
            e.printStackTrace();
            return ""; // Retourne une chaîne vide en cas d'erreur
        }
    }


}
