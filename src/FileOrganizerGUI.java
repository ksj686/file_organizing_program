import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizerGUI {
    private JFrame frame;
    private JTextField directoryField;
    private JTextArea logArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileOrganizerGUI::new);
    }

    public FileOrganizerGUI() {
        frame = new JFrame("파일 정리 도구");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        // 경로 입력 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        directoryField = new JTextField(20);
        JButton browseButton = new JButton("찾아보기");
        JButton organizeButton = new JButton("정리하기");

        inputPanel.add(new JLabel("디렉토리 경로:"));
        inputPanel.add(directoryField);
        inputPanel.add(browseButton);
        inputPanel.add(organizeButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // 로그 영역
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // "찾아보기" 버튼 이벤트 처리
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    directoryField.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        // "정리하기" 버튼 이벤트 처리
        organizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String directoryPath = directoryField.getText();
                organizeFiles(directoryPath);
            }
        });

        frame.setVisible(true);
    }

    private void organizeFiles(String directoryPath) {
        File directory = new File(directoryPath);
        logArea.setText(""); // 로그 초기화

        if (!directory.exists() || !directory.isDirectory()) {
            logArea.append("유효하지 않은 디렉토리입니다.\n");
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

                    // 파일 이름 변경
                    String newFileName = createNewFileName(file.getName(), folderName);
                    File newFile = new File(folders.get(folderName), newFileName);

                    // 파일 이동
                    try {
                        Path sourcePath = file.toPath();
                        Files.move(sourcePath, newFile.toPath());
                        logArea.append("파일 이동 및 이름 변경: " + file.getName() + " -> " + newFileName + "\n");
                    } catch (IOException e) {
                        logArea.append("파일 이동 실패: " + file.getName() + "\n");
                        e.printStackTrace();
                    }
                }
            }
        }

        logArea.append("파일 정리가 완료되었습니다.\n");
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        return lastIndexOfDot == -1 ? "" : name.substring(lastIndexOfDot + 1).toLowerCase();
    }

    private String createNewFileName(String originalName, String folderName) {
        String nameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
        return folderName + "_" + nameWithoutExtension + ".txt"; // 확장자는 txt로 고정
    }
}
