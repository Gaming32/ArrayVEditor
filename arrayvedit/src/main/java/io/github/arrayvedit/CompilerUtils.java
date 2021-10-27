package io.github.arrayvedit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompilerUtils {
    public static File compileSort(EditorFrame parent, File file) {
        Pattern packagePattern = Pattern.compile("^\\s*package ([a-zA-Z\\.]+);");
        String contents;
        try {
            contents = new String(Files.readAllBytes(file.toPath()));
        }
        catch (Exception e) {
            parent.showErrorMessage(e, "Compile Sort");
            return null;
        }
        Matcher matcher = packagePattern.matcher(contents);
        if (!matcher.find()) {
            parent.showErrorMessage("No package specifed", "Compile Sort");
            return null;
        }
        String packageName = matcher.group(1);
        String name = packageName + "." + file.getName().split("\\.")[0];
        File tempDir = new File("./cache/" + String.join("/", packageName.split("\\.")));
        tempDir.mkdirs();
        File destFile = new File(tempDir.getAbsolutePath() + "/" + file.getName());
        try {
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            parent.showErrorMessage(e, "Compile Sort");
            return null;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager jFileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> jFiles = jFileManager.getJavaFileObjects(destFile);
        // CharArrayWriter outputWriter = new CharArrayWriter();
        // int success = compiler.run(null, errorStream, null, destPath.getAbsolutePath());
        File outputLocation = new File("compile-" + name + ".txt");
        Writer outputWriter;
        try {
            boolean didNotExist = outputLocation.createNewFile();
            outputWriter = new PrintWriter(outputLocation);
            if (!didNotExist) { // File already existed; append to it
                outputWriter.write("\n\n\n");
            }
        } catch (IOException e) {
            parent.showErrorMessage(e, "Compile Sort");
            return null;
        }
        boolean success = compiler.getTask(
            outputWriter,
            jFileManager,
            null,
            Arrays.asList(
                "-cp", parent.jarFile.getAbsolutePath()
            ),
            null,
            jFiles
        ).call();
        if (!success) {
            parent.showErrorMessage(String.format(
                "Failed to compile: %1$s\nAdditional information saved to: %2$s",
                destFile.getPath(), outputLocation.getAbsolutePath()
            ), "Compile Sort");
            return null;
        }
        return new File(Utils.getExtless(destFile) + ".class");
    }
}
