package mariemoore.PDFGenerator.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.itextpdf.text.Font;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import mariemoore.PDFGenerator.aop.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PDFStamper {
    @LogExecutionTime
    public static byte[] stampPDFWithPagination(byte[] document) {
        byte[] stampedDocument = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfReader pdfReader = new PdfReader(document);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            int pageCount = pdfReader.getNumberOfPages();

            for (int i = 1; i <= pageCount; i++) {
                PdfContentByte canvas = pdfStamper.getOverContent(i);
                canvas.setColorFill(BaseColor.BLACK);


                // Create the stamp text with page number
                String pageNumber = "Page " + i + " / " + pageCount;

                // Load the Helvetica font using the font file path
                BaseFont helvetica = BaseFont.createFont("/static/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

                // Create a Font with the Helvetica BaseFont
                Font font = new Font(helvetica, 9);

                Phrase pagePhrase = new Phrase(pageNumber, font);

                float x = pdfReader.getPageSize(i).getRight() - 85;
                float y = pdfReader.getPageSize(i).getBottom(50);

                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pagePhrase, x, y, 0);

            }
            pdfStamper.close();
            pdfReader.close();
            stampedDocument = outputStream.toByteArray();
        }
        catch (IOException | DocumentException e) {
            log.error("une erreur est survenue", e);
        }
        return stampedDocument;
    }

    public byte[] embedFontInPDF(byte[] document, String fontName) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Load the custom font as a BaseFont
            BaseFont customFont = BaseFont.createFont("/static/" + fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(customFont, 9);

            PdfReader pdfReader = new PdfReader(document);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            PdfContentByte canvas = pdfStamper.getOverContent(1);
            canvas.setColorFill(BaseColor.WHITE);

            Phrase pagePhrase = new Phrase(".", font);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pagePhrase, 10, 10, 0);

            // Close the PdfStamper to save changes and embed the font
            pdfStamper.close();
            pdfReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
    @LogExecutionTime
    public byte[] stampPDFWithAddressWrapped(byte[] document, Collection<String> adresseCotisant) {
        byte[] stampedDocument = null;
        try {
            // Constants for limited width (7 centimeters)
            float maxWidth = 7 * 28.35f; // Convert centimeters to points (1 cm = 28.35 points)
            float lineSpacing = 15; // Line spacing for each line of the address

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfReader pdfReader = new PdfReader(document);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            PdfContentByte canvas = pdfStamper.getOverContent(1);
            canvas.setColorFill(BaseColor.BLACK);

            // Load the Helvetica font using the font file path
            BaseFont helvetica = BaseFont.createFont("/static/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Create a Font with the Helvetica BaseFont
            Font font = new Font(helvetica, 9);


            // Set position for address
            float x = 280;
            float y = 710;


            for (String line : adresseCotisant) {
                // Split the line into words to handle text wrapping
                String[] words = line.split("\\s+");
                StringBuilder wrappedLine = new StringBuilder();
                float lineWidth = 0;

                for (String word : words) {
                    // Calculate the width of the word in points
                    float wordWidth = font.getCalculatedBaseFont(false).getWidthPoint(word, 10);

                    // Check if adding the word exceeds the maxWidth
                    if (lineWidth + wordWidth > maxWidth) {
                        // If adding the word exceeds the maxWidth, draw the current wrappedLine
                        Phrase phrase_addressLine = new Phrase(wrappedLine.toString(), font);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_addressLine, x, y, 0);
                        y -= lineSpacing; // Move to the next line
                        wrappedLine.setLength(0); // Clear the wrappedLine for the next line
                        lineWidth = 0;
                    }

                    wrappedLine.append(word).append(" ");
                    lineWidth += wordWidth + font.getCalculatedBaseFont(false).getWidthPoint(" ", 10); // Account for space
                }

                // Draw the last wrappedLine for the line of address
                Phrase phrase_addressLine = new Phrase(wrappedLine.toString(), font);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_addressLine, x, y, 0);
                y -= lineSpacing; // Move to the next line
            }

            pdfStamper.close();
            pdfReader.close();
            stampedDocument = outputStream.toByteArray();
        }
        catch (IOException | DocumentException e) {
            log.error("une erreur est survenue", e);
        }
        return stampedDocument;
    }

    @LogExecutionTime
    public byte[] stampPDFWithImage(byte[] document, String imagePath, double imgWidth, Integer x, Integer y) {
        byte[] stampedDocument = null;
        try {
            // Create an Image instance from the URL
            Image image = Image.getInstance(new URL(imagePath));

            // Stamp the image on the PDF
            PdfReader pdfReader = new PdfReader(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            PdfContentByte canvas = pdfStamper.getOverContent(1); // Choose the page number to stamp the image

            // Calculate the image height to maintain the aspect ratio
            float imageWidthInPoints = (float)imgWidth * 72 / 2.54f; // Convert cm to points (72 points per inch, 2.54cm per inch)

            float aspectRatio = image.getWidth() / image.getHeight();
            float imageHeightInPoints = imageWidthInPoints / aspectRatio;

            // Set the image position
            Rectangle pageSize = pdfReader.getPageSize(1);

            image.setAbsolutePosition(x, y);
            image.scaleAbsolute(imageWidthInPoints, imageHeightInPoints);

            canvas.addImage(image);

            pdfStamper.close();
            pdfReader.close();
            stampedDocument = outputStream.toByteArray();
        }
        catch (IOException | DocumentException e) {
            log.error("une erreur est survenue", e);
        }
        return stampedDocument;
    }

    @LogExecutionTime
    public byte[] stampPDFWithFirstnameLastnameAddressWrapped(
            byte[] document,
            String civiliteMembre,
            String nomPrenom,
            Collection<String> adresseCotisant,
            Integer pageToStamp) {
        byte[] stampedDocument = null;
        try {
            // Constants for limited width (7 centimeters)
            float maxWidth = 7 * 28.35f; // Convert centimeters to points (1 cm = 28.35 points)
            float lineSpacing = 15; // Line spacing for each line of the address

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfReader pdfReader = new PdfReader(document);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            PdfContentByte canvas = pdfStamper.getOverContent(pageToStamp);
            canvas.setColorFill(BaseColor.BLACK);

            // Load the Helvetica font using the font file path
            BaseFont helvetica = BaseFont.createFont("/static/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Create a Font with the Helvetica BaseFont
            Font font = new Font(helvetica, 9);

            // Set position for civil status, first name, and last name //TO DO => ramener de 0.5 cm sur la gauche
            float x = 328;
            float nameY = 690;

            // Split the name line into words to handle text wrapping
            String[] nameWords;
            if(civiliteMembre != null){
                nameWords = (civiliteMembre + " " + nomPrenom).split("\\s+");
            }
            else{
                nameWords = nomPrenom.split("\\s+");
            }
            StringBuilder wrappedNameLine = new StringBuilder();
            float nameLineWidth = 0;

            for (String word : nameWords) {
                // Calculate the width of the word in points
                float wordWidth = font.getCalculatedBaseFont(false).getWidthPoint(word, 10);

                // Check if adding the word exceeds the maxWidth
                if (nameLineWidth + wordWidth > maxWidth) {
                    // If adding the word exceeds the maxWidth, draw the current wrappedNameLine
                    Phrase phrase_nameLine = new Phrase(wrappedNameLine.toString(), font);
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_nameLine, x, nameY, 0);
                    nameY -= lineSpacing; // Move to the next line
                    wrappedNameLine.setLength(0); // Clear the wrappedNameLine for the next line
                    nameLineWidth = 0;
                }

                wrappedNameLine.append(word).append(" ");
                nameLineWidth += wordWidth + font.getCalculatedBaseFont(false).getWidthPoint(" ", 10); // Account for space
            }

            // Draw the last wrappedNameLine for the name
            Phrase phrase_nameLine = new Phrase(wrappedNameLine.toString(), font);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_nameLine, x, nameY, 0);

            // Set position for address
            float addressX = x;
            float addressY = nameY - lineSpacing;

            for (String line : adresseCotisant) {
                // Split the address line into words to handle text wrapping
                String[] addressWords = line.split("\\s+");
                StringBuilder wrappedAddressLine = new StringBuilder();
                float addressLineWidth = 0;

                for (String word : addressWords) {
                    // Calculate the width of the word in points
                    float wordWidth = font.getCalculatedBaseFont(false).getWidthPoint(word, 10);

                    // Check if adding the word exceeds the maxWidth
                    if (addressLineWidth + wordWidth > maxWidth) {
                        // If adding the word exceeds the maxWidth, draw the current wrappedAddressLine
                        Phrase phrase_addressLine = new Phrase(wrappedAddressLine.toString(), font);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_addressLine, addressX, addressY, 0);
                        addressY -= lineSpacing; // Move to the next line
                        wrappedAddressLine.setLength(0); // Clear the wrappedAddressLine for the next line
                        addressLineWidth = 0;
                    }

                    wrappedAddressLine.append(word).append(" ");
                    addressLineWidth += wordWidth + font.getCalculatedBaseFont(false).getWidthPoint(" ", 10); // Account for space
                }

                // Draw the last wrappedAddressLine for the address
                Phrase phrase_addressLine = new Phrase(wrappedAddressLine.toString(), font);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase_addressLine, addressX, addressY, 0);
                addressY -= lineSpacing; // Move to the next line
            }

            pdfStamper.close();
            pdfReader.close();
            stampedDocument = outputStream.toByteArray();
        }
        catch (IOException | DocumentException e) {
            log.error("une erreur est survenue", e);
        }
        return stampedDocument;
    }
}
