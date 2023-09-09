package com.pk.transactionservice.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.swing.text.Document;

@Component
public class PdfConfig {

    public final float FIRST_LINE_START_X = 50;
    public final float PAGE_LEADING = 12F;
    public final float SECTION_TITLE_FONT_SIZE = 16F;
    public final float NORMAL_TEXT_FONT_SIZE = 12F;

    public void renderText(PDPageContentStream pdStream, String text, float fontSize, PDSimpleFont font) throws Exception {
        pdStream.setFont(font, fontSize);
        pdStream.showText(text);
        pdStream.newLine();
    }
}
