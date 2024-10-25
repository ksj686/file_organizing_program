import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizer {
    public static void main(String[] args) {
        String directoryPath = "C:/test/test/test"; // 정리할 디렉토리 경로
        organizeFiles(directoryPath);
    }

    public static void organizeFiles(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("유효하지 않은 디렉토리입니다.");
            return;
        }

        Map<String, File> folders = new HashMap<>();

        // 디렉토리 내의 모든 파일 가져오기
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String extension = getFileExtension(file);
                    String folderName = extension.isEmpty() ? "기타" : extension;

                    // 폴더가 존재하지 않으면 생성
                    if (!folders.containsKey(folderName)) {
                        File newFolder = new File(directory, folderName);
                        if (newFolder.mkdir()) {
                            folders.put(folderName, newFolder);
                        }
                    }

                    // 파일 이동
                    try {
                        Path sourcePath = file.toPath();
                        Path targetPath = Paths.get(folders.get(folderName).getAbsolutePath(), file.getName());
                        Files.move(sourcePath, targetPath);
                        System.out.println("파일 이동: " + file.getName() + " -> " + folderName);
                    } catch (IOException e) {
                        System.out.println("파일 이동 실패: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("파일 정리가 완료되었습니다.");
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        return lastIndexOfDot == -1 ? "" : name.substring(lastIndexOfDot + 1).toLowerCase();
    }
}
