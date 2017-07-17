package pae.alimentos.controllers;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.ConditionalStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;

import pae.alimentos.models.Alimento;
import pae.dbconnections.PostgresDbConnection;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Pdf {

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;
    private List<Alimento> insumos;
    Connection connection = null;

    public void buildInventario() throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alimentos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<String> nombreColumn = col.column("Nombre", "nombre", type.stringType());
        TextColumnBuilder<Double> cantidadColumn = col.column("Cantidad", "cantidad", type.doubleType());
        TextColumnBuilder<String> tipoColumn = col.column("Medida", "descripcion", type.stringType());

        report.columns(nombreColumn, cantidadColumn, tipoColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                        .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                        "Inventario de Insumos\n" +
                        String.valueOf(localDate) + "\n " +
                        time + "\n").setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setDataSource("SELECT nombre, cantidad, descripcion FROM alimentos  ORDER BY id",connection);
        report.show(false);
    }

    public void buildReport(String fechaInicial, String fechaFinal) throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alimentos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        TextColumnBuilder<String> insumoColumn = col.column("Insumo", "insumo", type.stringType());
        TextColumnBuilder<String> accionColumn = col.column("Acción", "accion", type.stringType());
        TextColumnBuilder<Double> cantidadColumn = col.column("Cantidad", "cantidad", type.doubleType());
        TextColumnBuilder<String> tipoColumn = col.column("Medida", "descripcion", type.stringType());
        TextColumnBuilder<Date> fechaColumn = col.column("Fecha", "fecha", type.dateType());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        ConditionalStyleBuilder rowStyle1 = stl.conditionalStyle(cnd.equal(accionColumn,"Abastecer"))
                .setBackgroundColor(new Color(210,255,210));
        ConditionalStyleBuilder rowStyle2 = stl.conditionalStyle(cnd.equal(accionColumn,"Consumir"))
                .setBackgroundColor(new Color(255,210,210));
        ConditionalStyleBuilder rowStyle3 = stl.conditionalStyle(cnd.equal(accionColumn,"Crear"))
                .setBackgroundColor(new Color(210,210,255));
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        Bar3DChartBuilder bar3DChartBuilder = cht.bar3DChart().setTitle("\n\nReporte de Movimiento de Insumos")
                .setCategory(insumoColumn)
                .addSerie(
                        cht.serie(cantidadColumn)
                );

        report.columns(insumoColumn, accionColumn, cantidadColumn, tipoColumn, fechaColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().detailRowHighlighters(rowStyle1,rowStyle2,rowStyle3)
                .setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                                "Reporte de Movimiento de Insumos\n" +
                                String.valueOf(localDate) + "\n " +
                                time + "\n").setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .summary(bar3DChartBuilder)
                .setDataSource("SELECT alimentos.nombre as insumo, " +
                        "inventarios.accion, " +
                        "inventarios.cantidad, " +
                        "alimentos.descripcion as descripcion, " +
                        "inventarios.fecha " +
                        "FROM public.inventarios, public.alimentos " +
                        "WHERE inventarios.id_alimento = alimentos.id " +
                        "AND inventarios.fecha BETWEEN '"+fechaInicial+"' AND '"+fechaFinal+"' " +
                        "ORDER BY inventarios.fecha",connection);
        report.show(false);
    }
}