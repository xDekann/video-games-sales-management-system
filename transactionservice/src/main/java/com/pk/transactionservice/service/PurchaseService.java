package com.pk.transactionservice.service;

import com.pk.transactionservice.config.PdfConfig;
import com.pk.transactionservice.model.entity.PurchaseDetailsDto;
import com.pk.transactionservice.model.entity.PurchaseDto;
import com.pk.transactionservice.model.entity.PurchaseDtoPaginated;
import com.pk.transactionservice.model.entity.purchase.CartItem;
import com.pk.transactionservice.model.entity.purchase.Purchase;
import com.pk.transactionservice.model.entity.purchase.Status;
import com.pk.transactionservice.repository.PurchaseRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final PdfConfig pdfConfig;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, PdfConfig pdfConfig) {
        this.purchaseRepository = purchaseRepository;
        this.pdfConfig = pdfConfig;
    }

    public PurchaseDtoPaginated getPurchases(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        Page<Purchase> receivedProducts =  purchaseRepository.findAllByUsernameOrderByTransactionDateDesc(username, pageable);
        List<Purchase> purchaseList = receivedProducts.getContent();
        return PurchaseDtoPaginated.builder()
                .purchases(purchaseList.stream()
                .map(purchase -> PurchaseDto.builder()
                        .id(purchase.getTransactionId())
                        .deliveryMethod(purchase.getDeliveryMethod())
                        .value(purchase.getTransactionValue())
                        .date(dateFormat.format(purchase.getTransactionDate()))
                        .status(purchase.getStatus().toString())
                        .items(purchase.getPurchaseItems().stream()
                                .map(cartItem -> PurchaseDetailsDto.builder()
                                        .name(cartItem.getName())
                                        .price(cartItem.getPrice())
                                        .amount(cartItem.getAmount())
                                        .build())
                                .toList())
                        .build())
                .toList())
                .totalPages(receivedProducts.getTotalPages())
                .build();
    }

    public byte[] getTransactionPdf(String transactionId) throws Exception {
        Purchase purchase = purchaseRepository.findByTransactionId(transactionId);
        if (purchase == null || !purchase.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())
                || purchase.getStatus() != Status.SUCCESS) {
            return null;
        }

        PDDocument document = new PDDocument();
        PDPage pdfPage = new PDPage(PDRectangle.A4);
        document.addPage(pdfPage);
        final float FIRST_LINE_START_Y = pdfPage.getMediaBox().getHeight() - 50;
        PDPageContentStream contentStream = new PDPageContentStream(document, pdfPage);
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, pdfConfig.SECTION_TITLE_FONT_SIZE);
        contentStream.setLeading(pdfConfig.PAGE_LEADING);
        contentStream.newLineAtOffset(pdfConfig.FIRST_LINE_START_X , FIRST_LINE_START_Y);
        contentStream.showText("Transaction invoice - " + purchase.getTransactionId());
        contentStream.newLine();
        
        // Client username
        contentStream.newLine();
        pdfConfig.renderText(contentStream,"Client: " + purchase.getUsername(), pdfConfig.SECTION_TITLE_FONT_SIZE, PDType1Font.HELVETICA);
        contentStream.newLine();

        // Client details
        pdfConfig.renderText(contentStream, "Details:", pdfConfig.SECTION_TITLE_FONT_SIZE, PDType1Font.HELVETICA_BOLD);
        contentStream.newLine();
        pdfConfig.renderText(contentStream, "Name: " + purchase.getUserDetails().getName(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Surname: " + purchase.getUserDetails().getSurname(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Email: " + purchase.getUserDetails().getEmail(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "City: " + purchase.getUserDetails().getCity(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Address: " + purchase.getUserDetails().getAddress(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        contentStream.newLine();

        // Transaction details
        pdfConfig.renderText(contentStream, "Transaction details:", pdfConfig.SECTION_TITLE_FONT_SIZE, PDType1Font.HELVETICA_BOLD);
        contentStream.newLine();
        pdfConfig.renderText(contentStream, "Total price: " + purchase.getTransactionValue() + " PLN", pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Delivery method: " + purchase.getDeliveryMethod(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Date: " + dateFormat.format(purchase.getTransactionDate()), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        pdfConfig.renderText(contentStream, "Payment method: " + purchase.getPaymentMethod(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
        contentStream.newLine();

        // Item details
        pdfConfig.renderText(contentStream, "Items:", pdfConfig.SECTION_TITLE_FONT_SIZE, PDType1Font.HELVETICA_BOLD);
        contentStream.newLine();
        for (CartItem item : purchase.getPurchaseItems()) {
            pdfConfig.renderText(contentStream, item.getName(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
            pdfConfig.renderText(contentStream, "Price: " + item.getPrice(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
            pdfConfig.renderText(contentStream, "Amount: " + item.getAmount(), pdfConfig.NORMAL_TEXT_FONT_SIZE, PDType1Font.HELVETICA);
            contentStream.newLine();
        }
        contentStream.newLine();
        pdfConfig.renderText(contentStream, "by Video Games Sales Management System", pdfConfig.SECTION_TITLE_FONT_SIZE, PDType1Font.HELVETICA_BOLD);

        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        document.save(byteOutput);
        document.close();

        return byteOutput.toByteArray();
    }
}
