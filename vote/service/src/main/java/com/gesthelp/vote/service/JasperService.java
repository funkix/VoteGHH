package com.gesthelp.vote.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Scrutin;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Service
@Log
public class JasperService implements JasperParameters {
	@Autowired
	private DataSource dataSource;

	@Value("${jasper.jasperPath.emargement}")
	private String jasperReportPath;

	@Value("${jasper.exportPath}")
	private String jasperExportFilePath;

	public byte[] exportEmargementReport(Scrutin s, boolean recette) throws JRException, SQLException, IOException {
		log.info("exportEmargementReport IN " + s.getId() + " recette=" + recette);
		Map<String, Object> parameters = this.emargementReportParams(s, recette);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		JasperPrint jasperPrint = JasperFillManager.fillReport(getEmargementReport(), parameters, dataSource.getConnection());
		JRPdfExporter exporter = getByteExporter(jasperPrint, parameters, pdfOutputStream);
		exporter.exportReport();
		return pdfOutputStream.toByteArray();
	}

	public void exportEmargementReportFile(Scrutin s, String fileName, boolean recette) throws JRException, SQLException, IOException {
		log.info("exportEmargementReportFile IN " + s.getId() + " recette=" + recette);
		Map<String, Object> parameters = this.emargementReportParams(s, recette);
		JasperPrint jasperPrint = JasperFillManager.fillReport(getEmargementReport(), parameters, dataSource.getConnection());
		JRPdfExporter exporter = getFileExporter(jasperPrint, parameters, new File(this.jasperExportFilePath, "emarg_scrut_" + s.getId() + ".pdf"));
		exporter.exportReport();
	}

	private JasperReport getEmargementReport() throws JRException, IOException {
		return (JasperReport) JRLoader.loadObject(new ClassPathResource(jasperReportPath).getFile());
	}

	private Map<String, Object> emargementReportParams(Scrutin s, boolean recette) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(SCRUTIN_NATURE, s.getNature());
		parameters.put(SCRUTIN_ID, s.getId());
		parameters.put(SCRUTIN_IS_PRODUCTION, !recette);
		parameters.put(SCRUTIN_ROLE_UTILISATEUR, recette ? SCRUTIN_ROLE_VOTANT_RECETTE : SCRUTIN_ROLE_VOTANT_PRODUCTION);
		parameters.put(SCRUTIN_DATE_OUVERTURE, java.util.Date.from(s.getDateOuverture().atZone(ZoneId.systemDefault()).toInstant()));
		parameters.put(SCRUTIN_DATE_FERMETURE, java.util.Date.from(s.getDateFermeture().atZone(ZoneId.systemDefault()).toInstant()));
		parameters.put(SCRUTIN_COLLEGE_HASH, null /* TODO */);
		return parameters;
	}

	private JRPdfExporter getByteExporter(JasperPrint jasperPrint, Map<String, Object> parameters, ByteArrayOutputStream pdfOutputStream)
			throws JRException, SQLException {
		JRPdfExporter exporter = getExporter(jasperPrint, parameters);
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
		return exporter;
	}

	private JRPdfExporter getFileExporter(JasperPrint jasperPrint, Map<String, Object> parameters, File exportFile) throws JRException, SQLException {
		JRPdfExporter exporter = getExporter(jasperPrint, parameters);
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportFile));
		return exporter;
	}

	private JRPdfExporter getExporter(JasperPrint jasperPrint, Map<String, Object> parameters) throws JRException, SQLException {
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("Gest'Help");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);
		return exporter;
	}

}
