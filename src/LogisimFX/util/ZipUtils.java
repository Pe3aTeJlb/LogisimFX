package LogisimFX.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static void zipFolder(final File folder, final File zipFile) throws IOException {
		zipFolder(folder, new FileOutputStream(zipFile));
	}

	public static void zipFolder(final File folder, final OutputStream outputStream) throws IOException {
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
			processFolder(folder, zipOutputStream, folder.getPath().length() + 1);
		}
	}

	private static void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength)
			throws IOException {
		for (final File file : folder.listFiles()) {
			if (file.isFile()) {
				final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
				zipOutputStream.putNextEntry(zipEntry);
				try (FileInputStream inputStream = new FileInputStream(file)) {
					IOUtils.copy(inputStream, zipOutputStream);
				}
				zipOutputStream.closeEntry();
			} else if (file.isDirectory()) {
				processFolder(file, zipOutputStream, prefixLength);
			}
		}
	}

	public static void unzipFolder(final File zipFile, final File folder) throws IOException {
		unzipFolder(new FileInputStream(zipFile), folder.toPath());
	}

	public static void unzipFolder(FileInputStream is, Path targetDir) throws IOException {
		targetDir = targetDir.toAbsolutePath();
		try (ZipInputStream zipIn = new ZipInputStream(is)) {
			for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
				Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
				if (!resolvedPath.startsWith(targetDir)) {
					// see: https://snyk.io/research/zip-slip-vulnerability
					throw new RuntimeException("Entry with an illegal path: "
							+ ze.getName());
				}
				if (ze.isDirectory()) {
					Files.createDirectories(resolvedPath);
				} else {
					Files.createDirectories(resolvedPath.getParent());
					Files.copy(zipIn, resolvedPath);
				}
			}
		}
	}

	public static boolean isZip(File f) {
		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}


}
