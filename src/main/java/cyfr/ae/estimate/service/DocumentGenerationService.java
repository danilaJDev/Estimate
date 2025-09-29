package cyfr.ae.estimate.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import cyfr.ae.estimate.dto.EstimateResponseDto;
import cyfr.ae.estimate.dto.EstimateItemResponseDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentGenerationService {

    private static final Font HEADING_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12);

    public ByteArrayInputStream generatePdf(EstimateResponseDto estimate) throws DocumentException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Header
        // TODO: Add company logo from resources
        // Image logo = Image.getInstance("classpath:static/images/logo.png");
        // document.add(logo);

        document.add(new Paragraph("Estimate #" + estimate.getId(), HEADING_FONT));
        document.add(new Paragraph("Project: " + estimate.getProjectName(), NORMAL_FONT));
        document.add(new Paragraph("Date: " + estimate.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), NORMAL_FONT));
        document.add(Chunk.NEWLINE);

        // Table with items
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 2, 2, 2});

        // Table Header
        addTableCell(table, "#", BOLD_FONT);
        addTableCell(table, "Work/Service", BOLD_FONT);
        addTableCell(table, "Quantity", BOLD_FONT);
        addTableCell(table, "Price per unit", BOLD_FONT);
        addTableCell(table, "Total", BOLD_FONT);

        // Table Body
        int index = 1;
        for (EstimateItemResponseDto item : estimate.getItems()) {
            addTableCell(table, String.valueOf(index++), NORMAL_FONT);
            addTableCell(table, item.getPosition().getName(), NORMAL_FONT);
            addTableCell(table, item.getQuantity().toString() + " " + item.getPosition().getUnit(), NORMAL_FONT);
            addTableCell(table, formatCurrency(item.getPosition().getCustomerPrice()), NORMAL_FONT);
            addTableCell(table, formatCurrency(item.getTotal()), NORMAL_FONT);
        }
        document.add(table);
        document.add(Chunk.NEWLINE);

        // Totals
        addTotalRow(document, "Subtotal:", formatCurrency(estimate.getSubtotal()));
        if (estimate.getVatAmount().compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(document, "VAT (" + estimate.getVatRate() + "%):", formatCurrency(estimate.getVatAmount()));
        }
        if (estimate.getMarkupAmount().compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(document, "Markup:", formatCurrency(estimate.getMarkupAmount()));
        }
        addTotalRow(document, "Final Total:", formatCurrency(estimate.getFinalTotal()), BOLD_FONT);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTotalRow(com.lowagie.text.Document document, String label, String value) throws DocumentException {
        addTotalRow(document, label, value, NORMAL_FONT);
    }

    private void addTotalRow(com.lowagie.text.Document document, String label, String value, Font font) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label, BOLD_FONT));
        p.add(new Chunk(" " + value, font));
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
    }

    private String formatCurrency(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString() + " AED";
    }

    public ByteArrayInputStream generateDocx(EstimateResponseDto estimate) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XWPFDocument document = new XWPFDocument()) {
            // Title
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Estimate #" + estimate.getId());
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // Project Info
            XWPFParagraph projectInfo = document.createParagraph();
            projectInfo.createRun().setText("Project: " + estimate.getProjectName());
            projectInfo.createRun().addBreak();
            projectInfo.createRun().setText("Date: " + estimate.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // Table
            XWPFTable table = document.createTable(estimate.getItems().size() + 1, 5);
            table.setWidth("100%");

            // Header
            setCellText(table.getRow(0).getCell(0), "№", true);
            setCellText(table.getRow(0).getCell(1), "Work/Service", true);
            setCellText(table.getRow(0).getCell(2), "Quantity", true);
            setCellText(table.getRow(0).getCell(3), "Price per unit", true);
            setCellText(table.getRow(0).getCell(4), "Total", true);

            // Body
            int index = 0;
            for (EstimateItemResponseDto item : estimate.getItems()) {
                XWPFTableRow row = table.getRow(index + 1);
                setCellText(row.getCell(0), String.valueOf(index + 1), false);
                setCellText(row.getCell(1), item.getPosition().getName(), false);
                setCellText(row.getCell(2), item.getQuantity().toString() + " " + item.getPosition().getUnit(), false);
                setCellText(row.getCell(3), formatCurrency(item.getPosition().getCustomerPrice()), false);
                setCellText(row.getCell(4), formatCurrency(item.getTotal()), false);
                index++;
            }

            document.createParagraph().createRun().addBreak();

            // Totals
            addTotalRowWord(document, "Subtotal:", formatCurrency(estimate.getSubtotal()));
            if (estimate.getVatAmount().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRowWord(document, "VAT (" + estimate.getVatRate() + "%):", formatCurrency(estimate.getVatAmount()));
            }
            if (estimate.getMarkupAmount().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRowWord(document, "Markup:", formatCurrency(estimate.getMarkupAmount()));
            }
            addTotalRowWord(document, "Final Total:", formatCurrency(estimate.getFinalTotal()), true);

            document.write(out);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
    }

    private void addTotalRowWord(XWPFDocument document, String label, String value) {
        addTotalRowWord(document, label, value, false);
    }

    private void addTotalRowWord(XWPFDocument document, String label, String value, boolean bold) {
        XWPFParagraph p = document.createParagraph();
        p.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun labelRun = p.createRun();
        labelRun.setText(label + " ");
        labelRun.setBold(true);
        XWPFRun valueRun = p.createRun();
        valueRun.setText(value);
        valueRun.setBold(bold);
    }

    public ByteArrayInputStream generateXlsx(EstimateResponseDto estimate) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Estimate");

            // Header Font
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"#", "Work/Service", "Quantity", "Unit", "Price per unit", "Total"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Body Rows
            int rowNum = 1;
            for (EstimateItemResponseDto item : estimate.getItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(item.getPosition().getName());
                row.createCell(2).setCellValue(item.getQuantity().doubleValue());
                row.createCell(3).setCellValue(item.getPosition().getUnit());
                row.createCell(4).setCellValue(item.getPosition().getCustomerPrice().doubleValue());
                row.createCell(5).setCellValue(item.getTotal().doubleValue());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Totals
            rowNum += 2; // Add some space
            addTotalRowExcel(sheet, rowNum++, "Subtotal:", estimate.getSubtotal(), headerCellStyle);
             if (estimate.getVatAmount().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRowExcel(sheet, rowNum++, "VAT (" + estimate.getVatRate() + "%):", estimate.getVatAmount(), headerCellStyle);
            }
            if (estimate.getMarkupAmount().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRowExcel(sheet, rowNum++, "Markup:", estimate.getMarkupAmount(), headerCellStyle);
            }
            addTotalRowExcel(sheet, rowNum++, "Final Total:", estimate.getFinalTotal(), headerCellStyle);

            workbook.write(out);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTotalRowExcel(XSSFSheet sheet, int rowNum, String label, BigDecimal value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(4);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);

        Cell valueCell = row.createCell(5);
        valueCell.setCellValue(value.doubleValue());
    }
}