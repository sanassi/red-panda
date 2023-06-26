package com.example.demo.myide.domain.entity.AnyFeatures;

import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.Any;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Dist extends Any implements Feature {

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        /*
        if (fileToZip.isHidden()) {
            return;
        }
        */

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    @Override
    //fuction gitadd
    public ExecutionReport execute(Project project, Object... params) {

        // delete trash files
        project.getFeature(Mandatory.Features.Any.CLEANUP).get().execute(project);

        String projectName = project.getRootNode().getPath().getFileName().toString();
        File toZip = new File(project.getRootNode().getPath().toString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(project.getRootNode().getPath().toString() + ".zip");
        } catch (Exception e) {
            return new BadReport();
        }

        ZipOutputStream zipOut = new ZipOutputStream(fos);
        try {
            zipFile(toZip, toZip.getName(), zipOut);
        } catch (Exception e) {
            return new BadReport();
        }

        try {
            zipOut.close();
            fos.close();
        } catch (Exception e) {
            return new BadReport();
        }

        return new GoodReport();
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.DIST;
    }

}