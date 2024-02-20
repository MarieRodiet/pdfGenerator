package mariemoore.PDFGenerator.controller;

import mariemoore.PDFGenerator.util.DateUtil;
import mariemoore.PDFGenerator.aop.LogExecutionTime;
import mariemoore.PDFGenerator.pojo.LetterBean;
import mariemoore.PDFGenerator.util.PDFStamper;
import mariemoore.PDFGenerator.util.PdfGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping()
public class LetterController {
    private PdfGeneratorUtil generator;
    private PDFStamper pdfStamper;

    public LetterController(PdfGeneratorUtil generator, PDFStamper pdfStamper) {

        this.generator = generator;
        this.pdfStamper = pdfStamper;
    }

    @LogExecutionTime
    @ResponseBody
    @PostMapping("/letter")
    public ResponseEntity<?> getRenouvellementCourrier(@RequestBody LetterBean data) {
        LocalDate now = LocalDate.now();
        data.setDate(now.getDayOfMonth() + " " + DateUtil.getFrenchMonth(now.getMonth().getValue()) + " " + now.getYear());
        byte[] generatedDocument = this.generator.generatePdf("data", data, "letter");

        if(data.isAddressStamped()){
            //stamp address
            generatedDocument = this.pdfStamper.stampPDFWithFirstnameLastnameAddressWrapped(generatedDocument, data.getPrefix(), data.getName(), data.getAdress(), 1);
        }
        if(data.isPaginationStamped()){
            //stamp with pagination
            generatedDocument = this.pdfStamper.stampPDFWithPagination(generatedDocument);
        }
        //stamp image
        generatedDocument = this.pdfStamper.stampPDFWithImage(generatedDocument, "classpath:/static/images/bulle.png", 4, 435, 705);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "letter.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(generatedDocument);
    }
}
