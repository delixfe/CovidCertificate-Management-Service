package ch.admin.bag.covidcertificate.service.document;

import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This test is a dedicated tool for developers to compare the pdf rendering of different versions to identify rendering issues.
 */
@Disabled
@Slf4j
class PdfCompareTest {

    private static final String ROOT_PATH = "/home/dev/Downloads/";  // root folder for the compared folders


    @Test
    void compare_LATEST_with_MASTER() throws Exception {

        List<File> folders = Files.list(Paths.get(ROOT_PATH))
                .map(path -> path.toFile())
                .filter(file -> file.isDirectory() && !file.getName().equals("master") && !file.getName().equals("diff"))
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        compare("master", folders.get(folders.size()-1).getName());
    }

    @Test
    void compare_FIXED_with_MASTER() throws Exception {

        compare("master", "20211221_092437");
    }

    private void compare(String masterFolderName, String compareFolderName) throws Exception {

        Path masterFolder = Paths.get(ROOT_PATH, masterFolderName);
        if (!Files.exists(masterFolder)) {
            throw new IllegalStateException("master folder '"+masterFolder.toString()+"' is not existing");
        }
        Path compareFolder = Paths.get(ROOT_PATH, compareFolderName);
        if (!Files.exists(compareFolder)) {
            throw new IllegalStateException("compare folder '"+compareFolder.toString()+"' is not existing");
        }

        Map<String, File> masterPdfs = Files.list(masterFolder)
                .map(path -> path.toFile())
                .collect(Collectors.toMap(File::getName, p -> p));

        Path diffFolder = Paths.get(ROOT_PATH, "diff");
        if (Files.exists(diffFolder)) {
            FileUtils.deleteDirectory(diffFolder.toFile());
        }
        Files.createDirectory(diffFolder);

        Files.list(compareFolder)
            .map(path -> path.toFile())
            .parallel()
            .forEach(pdfToCompare -> {
                File masterPdf = masterPdfs.get(pdfToCompare.getName());
                if (masterPdf == null) {
                    log.warn("no master for pdf '{}'", pdfToCompare.getAbsoluteFile().toString());
                } else {
                    try {
                        compare(pdfToCompare, masterPdf, diffFolder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void compare(File pdfToCompare, File masterPdf, Path diffPath) throws IOException {

        PDFUtil pdfUtil = new PDFUtil();
        pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfUtil.highlightPdfDifference(true);

        pdfUtil.setImageDestinationPath(diffPath.toString());

        boolean pdfsAreEqual = pdfUtil.compare(pdfToCompare.getAbsolutePath(), masterPdf.getAbsolutePath());
        if (!pdfsAreEqual) {
            log.error("pdf '{}' differs (new: {}, old: {}, diff: {})",
                    pdfToCompare.toString().replace(pdfToCompare.getParent(), ""), pdfToCompare.getParent(), masterPdf.getParent(), diffPath.toString());
        }
    }

    private Path createFolder(String root, String folder) throws IOException {
        Path subFolder = Paths.get(root, folder);
        if (!Files.exists(subFolder)) {
            Files.createDirectory(subFolder);
        }
        return subFolder;
    }
}
