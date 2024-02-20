package mariemoore.PDFGenerator.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import mariemoore.PDFGenerator.aop.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;

@Slf4j
@Component
public class PdfGeneratorUtil {

    @Autowired
    TemplateEngine templateEngine;

    public PdfGeneratorUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @LogExecutionTime
    public byte[] generatePdf(String dataEntry, Object data, String template){
        Context context = new Context();
        context.setVariable(dataEntry, data);

        Timer timer1 = new Timer("Transformation to HTML");
        timer1.start();
        String html = this.templateEngine.process(template, context);
        timer1.end();
        log.info("execution dans generatePdf, {}", timer1.format());

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("classpath:/static/");
        converterProperties.setFontProvider(new DefaultFontProvider(true, true, true));

        Timer timer2 = new Timer("Transformation to PDF");
        timer2.start();
        HtmlConverter.convertToPdf(html, target, converterProperties);
        timer2.end();
        log.info("execution dans generatePdf, {}", timer2.format());

        byte[] bytes = target.toByteArray();
        return bytes;
    }
}
