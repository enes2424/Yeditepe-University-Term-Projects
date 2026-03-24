import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class P2P extends JFrame {
	private static final long serialVersionUID = 1L;
	public static String selfAddress;
	private static List<String> selfAddresses = new ArrayList<>();
	private SocketOperation peer;
	private JMenuBar menuBar = new JMenuBar();
	private JMenu streamMenu = new JMenu("Stream");
	private JMenuItem connectMenuItem = new JMenuItem("Connect");
	private JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
	private JMenuItem setRootVideoFolderMenuItem = new JMenuItem("Set Root Video Folder...");
	private JMenuItem setBufferFolderMenuItem = new JMenuItem("Set Buffer Folder...");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem aboutMenuItem = new JMenuItem("About");
	private Handler handler = new Handler();
	private JLabel statusLabel = new JLabel("Status: DISCONNECTED");
	private JLabel rootVideoFolder = new JLabel("Root Video Folder: (not set)");
	private JLabel bufferFolder = new JLabel("Buffer Folder: (not set)");
	private JSeparator separator = new JSeparator();
	private String sharedFolderPath = "";
	private String bufferFolderPath = "";
	private JFileChooser folderChooser = new JFileChooser();
	private boolean isConnect = false;
	private JLabel folderExclusion = new JLabel("Folder exclusion:");
	private DefaultListModel<String> excludedFoldersList = new DefaultListModel<>();
	private JList<String> excludedFoldersJList = new JList<>(excludedFoldersList);
	private JScrollPane excludedFoldersScrollPane = new JScrollPane(excludedFoldersJList);
	private JButton addExcludedFolderButton = new JButton("Add");
	private JButton delExcludedFolderButton = new JButton("Del");
	private JLabel excludeFilesMatchingTheseMasks = new JLabel("Exclude files matching masks:");
	private DefaultListModel<String> excludedMasksList = new DefaultListModel<>();
	private JList<String> excludedMasksJList = new JList<>(excludedMasksList);
	private JScrollPane excludedMasksScrollPane = new JScrollPane(excludedMasksJList);
	private JButton addExcludedMaskButton = new JButton("Add");
	private JButton delExcludedMaskButton = new JButton("Del");
	private String searchText = "";
	private JLabel searchVideosLabel = new JLabel("Search Videos:");
	private JTextField searchTextField = new JTextField();
	private JButton searchButton = new JButton("Search");
	private JLabel availableVideosLabel = new JLabel("Available Videos in Network:");
	private DefaultListModel<String> availableVideosList = new DefaultListModel<>();
	private JList<String> availableVideosJList = new JList<>(availableVideosList);
	private JScrollPane availableVideosScrollPane = new JScrollPane(availableVideosJList);
	private JLabel activeStreamsLabel = new JLabel("Active Streams:");
	private DefaultTableModel activeStreamsTableModel;
	private JTable activeStreamsTable;
	private JScrollPane activeStreamsScrollPane;
	private VideoPlayer videoPlayer;
	private JLabel eventLogLabel = new JLabel("Event Log:");
	private JTextArea eventLogArea = new JTextArea();
	private JScrollPane eventLogScrollPane = new JScrollPane(eventLogArea);
	private JLabel bufferStatusLabel = new JLabel("Global Buffer Status:");
	private javax.swing.JProgressBar bufferProgressBar = new javax.swing.JProgressBar(0, 100);
	private List<FileInfo> allListFileInfo = new ArrayList<>();
	private List<FileInfo> listFileInfo = new ArrayList<>();
	private List<String> allFoundFiles = new ArrayList<>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private FoundFilesManager foundFilesManager;

	static {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			boolean isSelfAddress;

			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;

				inetAddresses = networkInterface.getInetAddresses();
				isSelfAddress = true;
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress instanceof Inet4Address) {
						selfAddresses.add(inetAddress.getHostAddress());
						System.out.println("Detected self address: " + inetAddress.getHostAddress());
						if (isSelfAddress) {
							selfAddress = inetAddress.getHostAddress();
							isSelfAddress = false;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			new P2P();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void windowClosing() {
		if (!isConnect)
			System.exit(0);
		int response = JOptionPane.showConfirmDialog(P2P.this,
				"Are you sure you want to close the application?",
				"Close",
				JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.YES_OPTION) {
			if (videoPlayer != null) {
				videoPlayer.release();
			}
			peer.disconnect();
			System.exit(0);
		}
	}

	public P2P() throws UnknownHostException {
		super("P2P Video Streaming Application");

		peer = new SocketOperation(this);
		foundFilesManager = new FoundFilesManager(allListFileInfo, allFoundFiles);

		initializeWindow();
		initializeMenuBar();
		initializeStatusBar();
		initializeExclusionFilters();
		initializeSearchPanel();
		initializeAvailableVideosPanel();
		initializeActiveStreamsPanel();
		initializeVideoPlayerArea();
		initializeEventLog();
		setupListeners();

		setVisible(true);
	}

	private void initializeWindow() {
		setSize(1200, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		getContentPane().setBackground(new Color(245, 245, 245));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				P2P.this.windowClosing();
			}
		});
	}

	private void initializeMenuBar() {
		setJMenuBar(menuBar);

		menuBar.add(streamMenu);
		streamMenu.add(connectMenuItem);
		streamMenu.add(disconnectMenuItem);
		streamMenu.addSeparator();
		streamMenu.add(setRootVideoFolderMenuItem);
		streamMenu.add(setBufferFolderMenuItem);
		streamMenu.addSeparator();
		streamMenu.add(exitMenuItem);

		menuBar.add(helpMenu);
		helpMenu.add(aboutMenuItem);

		connectMenuItem.addActionListener(handler);
		disconnectMenuItem.addActionListener(handler);
		exitMenuItem.addActionListener(handler);
		setRootVideoFolderMenuItem.addActionListener(handler);
		setBufferFolderMenuItem.addActionListener(handler);
		aboutMenuItem.addActionListener(handler);
	}

	private void initializeStatusBar() {

		statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		statusLabel.setForeground(new Color(220, 53, 69));
		statusLabel.setBounds(20, 8, 200, 20);
		add(statusLabel);

		rootVideoFolder.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		rootVideoFolder.setForeground(new Color(102, 102, 102));
		rootVideoFolder.setBounds(20, 28, 500, 18);
		add(rootVideoFolder);

		bufferFolder.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		bufferFolder.setForeground(new Color(102, 102, 102));
		bufferFolder.setBounds(540, 28, 590, 18);
		add(bufferFolder);

		separator.setForeground(new Color(200, 200, 200));
		separator.setBounds(0, 45, 1200, 2);
		add(separator);

		FolderOperation.excludedFoldersList = excludedFoldersList;
		SocketOperation.excludedFoldersList = excludedFoldersList;
	}

	private void initializeExclusionFilters() {
		folderExclusion.setFont(new Font("Segoe UI", Font.BOLD, 10));
		folderExclusion.setForeground(new Color(51, 51, 51));
		folderExclusion.setBounds(20, 450, 150, 20);
		add(folderExclusion);

		excludedFoldersScrollPane.setBounds(20, 475, 195, 120);
		excludedFoldersScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		add(excludedFoldersScrollPane);

		addExcludedFolderButton.addActionListener(e -> handleAddFolderExclusion());
		addExcludedFolderButton.setFont(new Font("Segoe UI", Font.BOLD, 8));
		addExcludedFolderButton.setBounds(222, 475, 45, 28);
		addExcludedFolderButton.setBackground(new Color(70, 130, 180));
		addExcludedFolderButton.setForeground(Color.WHITE);
		addExcludedFolderButton.setFocusPainted(false);
		addExcludedFolderButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
		add(addExcludedFolderButton);

		delExcludedFolderButton.addActionListener(e -> handleDeleteFolderExclusion());
		delExcludedFolderButton.setFont(new Font("Segoe UI", Font.BOLD, 9));
		delExcludedFolderButton.setBounds(222, 508, 45, 28);
		delExcludedFolderButton.setBackground(new Color(220, 53, 69));
		delExcludedFolderButton.setForeground(Color.WHITE);
		delExcludedFolderButton.setFocusPainted(false);
		delExcludedFolderButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
		add(delExcludedFolderButton);

		excludeFilesMatchingTheseMasks.setFont(new Font("Segoe UI", Font.BOLD, 10));
		excludeFilesMatchingTheseMasks.setForeground(new Color(51, 51, 51));
		excludeFilesMatchingTheseMasks.setBounds(273, 450, 180, 20);
		add(excludeFilesMatchingTheseMasks);

		excludedMasksScrollPane.setBounds(273, 475, 195, 120);
		excludedMasksScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		add(excludedMasksScrollPane);

		addExcludedMaskButton.addActionListener(e -> handleAddFileMask());
		addExcludedMaskButton.setFont(new Font("Segoe UI", Font.BOLD, 8));
		addExcludedMaskButton.setBounds(475, 475, 45, 28);
		addExcludedMaskButton.setBackground(new Color(70, 130, 180));
		addExcludedMaskButton.setForeground(Color.WHITE);
		addExcludedMaskButton.setFocusPainted(false);
		addExcludedMaskButton.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		add(addExcludedMaskButton);

		delExcludedMaskButton.addActionListener(e -> handleDeleteFileMask());
		delExcludedMaskButton.setFont(new Font("Segoe UI", Font.BOLD, 8));
		delExcludedMaskButton.setBounds(475, 508, 45, 28);
		delExcludedMaskButton.setBackground(new Color(220, 53, 69));
		delExcludedMaskButton.setForeground(Color.WHITE);
		delExcludedMaskButton.setFocusPainted(false);
		delExcludedMaskButton.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		add(delExcludedMaskButton);
	}

	private void initializeSearchPanel() {
		searchVideosLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		searchVideosLabel.setForeground(new Color(51, 51, 51));
		searchVideosLabel.setBounds(20, 60, 150, 25);
		add(searchVideosLabel);

		searchTextField.setBounds(20, 90, 410, 30);
		searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		searchTextField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		add(searchTextField);

		searchButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
		searchButton.setBounds(438, 90, 82, 30);
		searchButton.setBackground(new Color(40, 167, 69));
		searchButton.setForeground(Color.WHITE);
		searchButton.setFocusPainted(false);
		searchButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
		searchButton.addActionListener(e -> {
			searchText = searchTextField.getText().toLowerCase().trim();
			performSearch();
		});
		add(searchButton);
	}

	private void initializeAvailableVideosPanel() {
		availableVideosLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		availableVideosLabel.setForeground(new Color(51, 51, 51));
		availableVideosLabel.setBounds(20, 130, 400, 25);
		add(availableVideosLabel);

		availableVideosScrollPane.setBounds(20, 160, 500, 280);
		availableVideosScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		availableVideosJList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		add(availableVideosScrollPane);
	}

	private void initializeActiveStreamsPanel() {
		activeStreamsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		activeStreamsLabel.setForeground(new Color(51, 51, 51));
		activeStreamsLabel.setBounds(540, 60, 640, 25);
		add(activeStreamsLabel);

		String[] columnNames = { "Video", "Source Peer", "Progress %", "Status" };
		activeStreamsTableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		activeStreamsTable = new JTable(activeStreamsTableModel);
		activeStreamsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		activeStreamsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
		activeStreamsTable.setRowHeight(25);
		activeStreamsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		activeStreamsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		activeStreamsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		activeStreamsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		activeStreamsTable.getColumnModel().getColumn(3).setPreferredWidth(100);

		activeStreamsScrollPane = new JScrollPane(activeStreamsTable);
		activeStreamsScrollPane.setBounds(540, 90, 640, 150);
		activeStreamsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		add(activeStreamsScrollPane);
	}

	private void initializeVideoPlayerArea() {
		videoPlayer = new VideoPlayer();
		videoPlayer.setBounds(540, 255, 640, 340);
		videoPlayer.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		videoPlayer.setBackground(Color.BLACK);
		add(videoPlayer);
	}

	private void initializeEventLog() {
		bufferStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		bufferStatusLabel.setForeground(new Color(51, 51, 51));
		bufferStatusLabel.setBounds(540, 605, 200, 25);
		add(bufferStatusLabel);

		bufferProgressBar.setBounds(540, 635, 640, 30);
		bufferProgressBar.setStringPainted(true);
		bufferProgressBar.setForeground(new Color(40, 167, 69));
		bufferProgressBar.setString("0% Buffered");
		add(bufferProgressBar);

		eventLogLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		eventLogLabel.setForeground(new Color(51, 51, 51));
		eventLogLabel.setBounds(20, 605, 200, 25);
		add(eventLogLabel);

		eventLogScrollPane.setBounds(20, 635, 500, 90);
		eventLogScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		add(eventLogScrollPane);
	}

	private void setupListeners() {
		availableVideosJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String selectedItem = availableVideosJList.getSelectedValue();
					int selectedIndex = availableVideosJList.getSelectedIndex();
					if (selectedItem == null || selectedIndex == -1)
						return;
					final String fileName = selectedItem.substring(0, selectedItem.indexOf(" from "));
					if (isValidDirectory(bufferFolderPath)) {
						int response = JOptionPane.showConfirmDialog(P2P.this,
								"Do you want to download file " + fileName + "?",
								"Download",
								JOptionPane.YES_NO_OPTION);

						if (response == JOptionPane.YES_OPTION) {
							activeStreamsTableModel.addRow(new Object[] { fileName, "", "0%", "Starting" });
							peer.addIDControlList();
							int index = activeStreamsTableModel.getRowCount() - 1;
							FileInfo fileInfo = listFileInfo.get(selectedIndex);
							logEvent("Started downloading: " + fileName);
							threadPool.execute(() -> {
								try {
									peer.download(bufferFolderPath, index, fileInfo);
									logEvent("Completed downloading: " + fileName);
								} catch (IOException | InterruptedException err) {
									JOptionPane.showMessageDialog(P2P.this, "Connection is broken!",
											"Download Cancelled", JOptionPane.WARNING_MESSAGE);
									logEvent("Download failed: " + fileName);
									err.printStackTrace();
								}
							});
						}
					} else {
						JOptionPane.showMessageDialog(P2P.this, "No suitable buffer folder found!",
								"Download Cancelled", JOptionPane.WARNING_MESSAGE);
						logEvent("Download cancelled: No buffer folder");
					}
					availableVideosJList.clearSelection();
				}
			}
		});

		activeStreamsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedRow = activeStreamsTable.getSelectedRow();
					if (selectedRow == -1)
						return;

					String file = ((String) activeStreamsTableModel.getValueAt(selectedRow, 0));

					String[] arr = ((String) activeStreamsTableModel.getValueAt(selectedRow, 0)).split(" ");
					int trimLength = arr[arr.length - 2].length() + arr[arr.length - 1].length() + 2;

					String fileName = file.substring(0, file.length() - trimLength);

					String status = (String) activeStreamsTableModel.getValueAt(selectedRow, 3);

					logEvent("Selected stream: " + fileName + " (Status: " + status + ")");

					if (status.equals("Completed") || status.equals("Streaming")) {
						String videoPath = bufferFolderPath + File.separator + fileName;
						File videoFile = new File(videoPath);
						if (videoFile.exists()) {
							playVideo(videoPath, fileName);
						}
					}
					activeStreamsTable.clearSelection();
				}
			}
		});
	}

	private void handleAddFolderExclusion() {
		if (isValidDirectory(sharedFolderPath)) {
			File directory = new File(sharedFolderPath);
			Path baseFolderPath = directory.toPath();
			List<String> folders = FolderOperation.getAllFolders(excludedFoldersList, baseFolderPath, directory);
			new FolderSelection(excludedFoldersList, folders);
		} else {
			JOptionPane.showMessageDialog(this, "No suitable shared folder found!",
					"Folder Exclusion Cancelled", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void handleDeleteFolderExclusion() {
		int selectedIndex = excludedFoldersJList.getSelectedIndex();
		if (selectedIndex != -1) {
			excludedFoldersList.remove(selectedIndex);
		}
	}

	private void handleAddFileMask() {
		String mask = JOptionPane.showInputDialog(this, "Enter the mask to exclude:", "Mask",
				JOptionPane.PLAIN_MESSAGE);
		if (mask != null && !mask.equals("") && !excludedMasksList.contains(mask)) {
			excludedMasksList.addElement(mask);
		}
	}

	private void handleDeleteFileMask() {
		int selectedIndex = excludedMasksJList.getSelectedIndex();
		if (selectedIndex != -1) {
			excludedMasksList.remove(selectedIndex);
		}
	}

	private boolean isValidDirectory(String text) {
		File directory = new File(text);
		if (directory.exists() && directory.isDirectory()) {
			return true;
		}
		return false;
	}

	public String getSharedFolder() {
		if (isValidDirectory(sharedFolderPath) == false)
			return "";
		return sharedFolderPath;
	}

	public static String formatBytes(double size) {
		String[] units = { "B", "KB", "MB", "GB", "TB" };
		int index = 0;

		while (size >= 1024 && index < units.length - 1) {
			size /= 1024.0;
			index++;
		}

		return String.format("%.2f %s", size, units[index]);
	}

	public boolean isMessageOwner(String address) {
		return selfAddresses.contains(address);
	}

	public void addElementToFoundList(String address, String receivedMessage) {
		SwingUtilities.invokeLater(() -> {
			foundFilesManager.updateFoundFiles(address, receivedMessage);
			performSearch();
		});
	}

	private void performSearch() {
		availableVideosList.clear();
		listFileInfo.clear();
		boolean isSearchTextEmpty = searchText.isEmpty();

		for (int i = 0; i < allListFileInfo.size(); i++) {
			String filename = allListFileInfo.get(i).filename;
			if (!FolderOperation.isMatchedFileAndMasks(filename, excludedMasksList)
					&& (isSearchTextEmpty || filename.toLowerCase().contains(searchText.toLowerCase()))) {
				listFileInfo.add(allListFileInfo.get(i));
				availableVideosList.addElement(allFoundFiles.get(i));
			}
		}
	}

	public void logEvent(String message) {
		SwingUtilities.invokeLater(() -> {
			String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
			eventLogArea.append("[" + timestamp + "] " + message + "\n");
			eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
		});
	}

	private class Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == exitMenuItem)
				windowClosing();
			else if (event.getSource() == connectMenuItem) {
				if (!isConnect) {
					isConnect = true;
					statusLabel.setText("Status: CONNECTED");
					statusLabel.setForeground(new Color(40, 167, 69));
					peer.connect();
					logEvent("Connected to network");
				}
			} else if (event.getSource() == disconnectMenuItem) {
				if (isConnect) {
					isConnect = false;
					statusLabel.setText("Status: DISCONNECTED");
					statusLabel.setForeground(new Color(220, 53, 69));
					availableVideosList.clear();
					allFoundFiles.clear();
					peer.disconnect();
					logEvent("Disconnected from network");
				}
			} else if (event.getSource() == setRootVideoFolderMenuItem) {
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = folderChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFolder = folderChooser.getSelectedFile();

					if (!sharedFolderPath.equals(selectedFolder.getAbsolutePath()))
						excludedFoldersList.clear();

					sharedFolderPath = selectedFolder.getAbsolutePath();
					rootVideoFolder.setText("Root Video Folder: " + sharedFolderPath);
					logEvent("Root folder set: " + sharedFolderPath);
				}
			} else if (event.getSource() == setBufferFolderMenuItem) {
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = folderChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFolder = folderChooser.getSelectedFile();
					bufferFolderPath = selectedFolder.getAbsolutePath();
					bufferFolder.setText("Buffer Folder: " + bufferFolderPath);
					logEvent("Buffer folder set: " + bufferFolderPath);
				}
			} else if (event.getSource() == aboutMenuItem)
				JOptionPane.showMessageDialog(P2P.this,
						"Name : Oguzhan\nSurname : CAKAN\nSchool Number : 20210702128\n"
								+ "Email : oguzhan.cakan@std.yeditepe.edu.tr",
						"Developer Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setVideoInfo(InetAddress address, int num, int index, double l) {
		if (index >= 0 && index < activeStreamsTableModel.getRowCount()) {
			activeStreamsTableModel.setValueAt(address.getHostAddress() + " - " + num, index, 1);
			activeStreamsTableModel.setValueAt(String.format("%.2f%%", l * 100), index, 2);
			if (l == 1.0)
				activeStreamsTableModel.setValueAt("Completed", index, 3);
			else
				activeStreamsTableModel.setValueAt("Streaming", index, 3);

			updateBufferStatus((int) (l * 100));
		}
	}

	public void playVideo(String videoPath, String filename) {
		SwingUtilities.invokeLater(() -> {
			if (videoPlayer != null) {
				videoPlayer.playStreamingVideo(videoPath);
				logEvent("Started playing: " + filename);
			}
		});
	}

	public void onNewChunkDownloaded() {
		if (videoPlayer != null) {
			videoPlayer.onNewDataAvailable();
		}
	}

	public void onDownloadComplete() {
		if (videoPlayer != null) {
			videoPlayer.notifyDownloadComplete();
		}
	}

	private void updateBufferStatus(int percentage) {
		SwingUtilities.invokeLater(() -> {
			bufferProgressBar.setValue(percentage);
			bufferProgressBar.setString(percentage + "% Buffered");
			if (percentage >= 100) {
				bufferProgressBar.setForeground(new Color(40, 167, 69));
			} else if (percentage >= 50) {
				bufferProgressBar.setForeground(new Color(255, 193, 7));
			} else {
				bufferProgressBar.setForeground(new Color(220, 53, 69));
			}
		});
	}
}
