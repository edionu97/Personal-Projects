package Utils;

import Domain.Tema;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.TableView;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PDFPrinter {

    private String pdfFileName,title,separator;
    private int titleSize = 35,fontSize = 20,spacing = 50;
    private Tema tema = null;


    private Document createDocument(Rectangle pageType){
        return new Document(pageType);
    }

    private void addTitle(Document document) throws  Exception{
        Paragraph paragraph = new Paragraph();

        paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN,10,Font.BOLDITALIC));
        paragraph.add("Date: " + LocalDate.now().toString());
        paragraph.setAlignment(Element.ALIGN_RIGHT);

        document.add(paragraph);

        paragraph.clear();
        paragraph.setSpacingBefore(20);
        paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN,titleSize,Font.BOLD|Font.ITALIC|Font.UNDERLINE));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(title);

        document.add(paragraph);
    }

    private void addExtraInformation(Document document) throws  Exception{

        Paragraph paragraph = new Paragraph("The hardest homework is homework: " + tema.getNrTema() + " with deadline " + tema.getDeadline() + " and requirement " +tema.getCerinta(),
                new Font(Font.FontFamily.TIMES_ROMAN,fontSize,Font.UNDERLINE|Font.ITALIC)
        );

        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(spacing / 2);

        document.add(paragraph);

    }

    public void setTheHarderHomework(Tema homework){
        this.tema = homework;
    }

    public void setPdfFileName(String fileName){
        pdfFileName= fileName;
    }

    public void setPdfFileTitle(String title){
        this.title = title;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;

    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setRepresentSeparator(String separator){
        this.separator  = separator;
    }

    private <T> PdfPTable addTable(TableView <T> tableView, List < T > items, Function<T,String> represent){

        PdfPTable pdfPTable = new PdfPTable(tableView.getColumns().size());

        ArrayList <PdfPCell> arrayList = new ArrayList<>();

        tableView.getColumns().forEach(tTableColumn -> {

            PdfPCell cell = new PdfPCell(new Paragraph(tTableColumn.getText(),new Font(Font.FontFamily.TIMES_ROMAN,fontSize + 2,Font.BOLD|Font.ITALIC)));

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);

            arrayList.add(cell);
        });

        arrayList.forEach(pdfPTable::addCell);


        pdfPTable.setSpacingBefore(spacing);

        pdfPTable.setHorizontalAlignment(Element.ALIGN_CENTER);


        items.forEach(item->{

            String[] fields = represent.apply(item).split(separator);

            for (String field : fields) {

                PdfPCell cell  = new PdfPCell(new Paragraph(field,new Font(Font.FontFamily.TIMES_ROMAN,fontSize,Font.ITALIC)));

                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                pdfPTable.addCell(cell);
            }
        });

        return pdfPTable;
    }

    public <T> void printFile(Rectangle pageType, TableView <T> tableView,List <T> items,Function<T,String> represent) throws  Exception{

        Document document = createDocument(pageType);

        if(!pdfFileName.contains(".pdf"))pdfFileName = pdfFileName.concat(".pdf");

        PdfWriter.getInstance(document,new FileOutputStream(pdfFileName));

        document.open();

        addTitle(document);

        document.add(addTable(tableView,items,represent));

        if(tema != null) addExtraInformation(document);

        document.close();

    }

}
