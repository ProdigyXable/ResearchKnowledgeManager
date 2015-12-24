/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 * The UserInterface class provides all the details needed for maintaining the
 * system's UserInterface
 *
 * @author Xable Enterprises
 */
public class UserInterface extends javax.swing.JFrame
{

    private static final class StringErrorMessages
    {

        private static final String resaveErrorMessage = "Some problems have been encountered with some files so the file's contents were not processed. Usually this means the file was saved by a third-party application. \n"
                + "\n"
                + "Try any of the below options to resolve the issue:\n"
                + "\n"
                + "1. Open the file with the proper application and resave it. When resaving, choose try choosing an updated file format or save the file as a text (.txt) file.\n"
                + "\n"
                + "2. Remove and delete the file from the repository. When the Research Knowlege Manager encounters a missing file, you'll be given the option to remove it from the system.";

        private static final String missingErrorMessage = "Below are a list of files the Research Knowledge Manager noticed were missing!\n"
                + "\n"
                + "These files will be completely removed from the system the next time the Research Knowledge Manager closes.";

        private static final String otherErrorMessage = "Some problems have been encountered with some files. The Research Knowledge Manager could not pinpoint any specific issues with the file. \n"
                + "As such, the file's contents were not processed. These files may or may not fit your search query.\n "
                + "\n"
                + "Try any of the below options to resolve the issue:\n"
                + "\n"
                + "1. Open the file with the proper application and resave the file as another format. This could be an update version of the file, or as something compatible with the Research Knowledge Manager such as a text file"
                + "\n"
                + "2. Remove and delete the file from the repository. When the Research Knowlege Manager encounters a missing file, you'll be given the option to remove it from the system.";

    }

    public enum ErrorType
    {

        RESAVE, MISSING, OTHER

    }

    private boolean autoTagStatus;

    private final long minimumValidFileSize = 255L;

    /**
     * Determines if any data should be locally saved when the system exits
     */
    boolean exitNoWrite = false;

    boolean confirmDeleteTag = false;

    /**
     * A ResearchKnowledgeManager instance. Used to retrieve and maintain values
     * from the ResearchKnowledgeManager in the UserInterface.
     */
    ResearchKnowledgeManager rm;

    /**
     * Simple ListModel used to fill the statusMessages JList
     *
     * @see #newMessage(java.lang.String)
     */
    DefaultListModel statusMessages = new DefaultListModel();

    /**
     * Simple ListModel used to fill the TagItem JList
     *
     * @see #newTagItem(java.lang.String)
     */
    DefaultListModel TagItemListModel = new DefaultListModel();

    /**
     * Identifies the current job which is going to be executed by the system.
     * Jobs in this variable can be started, paused, resumed, and canceled
     */
    Thread executingThread;

    /**
     * Identifies the last selected item in the File Tree. When the user focuses
     * on an object outside the tree, the selection is lost so this object will
     * always contain the last selected item, if any item has been selected in
     * the tree beforehand.
     */
//    Object LastFileTreeObject;
    /**
     * Identifies the last selected item in the Tag Tree. When the user focuses
     * on an object outside the tree, the selection is lost so this object will
     * always contain the last selected item, if any item has been selected in
     * the tree beforehand.
     */
//    Object LastTagTreeObject;
    /**
     * Dynamically names the viewport border of TagListItemScrollPane
     */
    String tagSearchListName = "Tag Selection List";

    /**
     * Dynamically names the searchResults
     */
    String searchResultsName = "Search Results";

    /**
     * Creates new form UserInterface
     *
     * @param rm A ResearchKnowledgeManager instance. Used to retrieve and
     * maintain values from the ResearchKnowledgeManager in the UserInterface.
     * @see ResearchKnowledgeManager
     * @see #rm
     */
    public UserInterface(ResearchKnowledgeManager rm)
    {
        this.rm = rm;
        initComponents();
        handleState();

        this.customActionPane.removeAll();
        this.validate();
    }

    private void startTask()
    {
        if (this.executingThread != null && !executingThread.isAlive())
        {
            this.executingThread.start();
            this.newMessage("Starting task...");
        }
    }

    /**
     * Sets the various task buttons depending on the state of the system
     *
     * @see ResearchKnowledgeManager.activeState
     */
    final void handleState()
    {
        switch (rm.actionStatus)
        {
            case INACTIVE:
            {
                cancelButton.setEnabled(false);
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(false);
                break;
            }

            case ACTIVE:
            {
                cancelButton.setEnabled(true);
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(true);
                break;
            }

            case PAUSED:
            {
                cancelButton.setEnabled(true);
                resumeButton.setEnabled(true);
                pauseButton.setEnabled(false);
                break;
            }

        }
    }

    void updateSearchResultsName(int buffer)
    {
        SearchResultsListScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), this.searchResultsName + " (" + buffer + ")", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        SearchResultsListScrollPane.repaint();

    }

    void enableUI()
    {
        this.setEnabled(true);
    }

    void updateTagTree()
    {
        this.TagTree.setModel(new TagModelTree(this.rm.Tags));

    }

    void updateFileTree()
    {
        this.FileTree.setModel(new FileModelTree(this.rm.Files));

    }

    void statusMessagesBottom()
    {
        statusList.ensureIndexIsVisible(statusList.getModel().getSize() - 1);
    }

    private void addMessage(String message)
    {
        statusMessages.addElement(" " + message);

    }

    private void TagItemMessage(String message)
    {
        TagItemListModel.addElement(message);
        SwingUtilities.invokeLater(() ->
        {
            this.refreshTagItemList();
            this.TagItemSearchList.repaint();

        });

    }

    public void newMessage(String message)
    {

        SwingUtilities.invokeLater(() ->
        {
            addMessage(message);
        });

        // Scrolls to the bottom of the Jlist
        SwingUtilities.invokeLater(() ->
        {
            statusMessagesBottom();
        });
    }

    /**
     * Adds a message/line to the statusJList in its own thread
     *
     * @param message The string to add to the statusJList
     */
    public void newTagItem(String message)
    {
        SwingUtilities.invokeLater(() ->
        {
            TagItemMessage(message);
        });
    }

    public void refreshTagItemList()
    {

        SwingUtilities.invokeLater(() ->
        {
            this.TagItemListModel.clear();

            // List should already be ensured to no have duplicates
            for (TagClass Tag : rm.Tags)
            {
                if (!TagItemListModel.contains(Tag))
                {
                    this.TagItemListModel.addElement(Tag);
                }
            }

        });

    }

    public void autoIndexAll()
    {
        this.indexAllFilesMenuItemActionPerformed(null);
        startTask();
    }

    public void ErrorHandleFunction(Vector<FileClass> badFileList, ErrorType error_status)
    {
        DefaultListModel buffer = new DefaultListModel();

        for (FileClass fc : badFileList)
        {
            buffer.addElement(fc.toString());
        }

        if (error_status == UserInterface.ErrorType.RESAVE)
        {
            this.resaveErrorMessageTextArea.setText(UserInterface.StringErrorMessages.resaveErrorMessage);
            this.resaveFileJList.setModel(buffer);
            this.resaveErrorHandlingFilesDialog.setVisible(true);
        }

        else if (error_status == UserInterface.ErrorType.MISSING)
        {
            this.missingErrorMessageTextArea.setText(UserInterface.StringErrorMessages.missingErrorMessage);
            this.missingFileJList.setModel(buffer);
            this.missingErrorHandlingFilesDialog.setVisible(true);
        }

        else if (error_status == UserInterface.ErrorType.OTHER)
        {
            this.otherErrorMessageTextArea.setText(UserInterface.StringErrorMessages.otherErrorMessage);
            this.otherFileJList.setModel(buffer);
            this.otherErrorHandlingFilesDialog.setVisible(true);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        modifyKeywordsButton = new javax.swing.JButton();
        removeTagFromFileButton = new javax.swing.JButton();
        removeFileFromTagButton = new javax.swing.JButton();
        addTagToFileButton = new javax.swing.JButton();
        addFileToSystemButton = new javax.swing.JButton();
        addFileToTagButton = new javax.swing.JButton();
        newTagButton = new javax.swing.JButton();
        SearchResultsPopupMenu = new javax.swing.JPopupMenu();
        OpenFilePopupMenuItem = new javax.swing.JMenuItem();
        RootFolderPopupMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        AddTagFilePopupMenuItem = new javax.swing.JMenuItem();
        RemoveTagFilePopupMenuItem = new javax.swing.JMenuItem();
        deleteTagFromSystemButton = new javax.swing.JButton();
        newTagDialog = new javax.swing.JDialog();
        addTagDialogButton = new javax.swing.JButton();
        quitTagDialogButton = new javax.swing.JButton();
        userInputTagDialogTextField = new javax.swing.JTextField();
        instructionsTagDialogLabel = new javax.swing.JLabel();
        confirmTagDeleteDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        confirmDeleteTagButton = new javax.swing.JButton();
        cancelDeleteTagButton = new javax.swing.JButton();
        autoTagDialog = new javax.swing.JDialog();
        TagListItemScrollPane1 = new javax.swing.JScrollPane();
        TagListAutoTag = new javax.swing.JList();
        sensitivitySlider = new javax.swing.JSlider();
        autoTagSubmit = new javax.swing.JButton();
        tagKeyWordScrollPane = new javax.swing.JScrollPane();
        tagKeywordList = new javax.swing.JList();
        modifyKeywordsDialog = new javax.swing.JDialog();
        modifyKeywordScrollPane = new javax.swing.JScrollPane();
        modifyKeywordsTextArea = new javax.swing.JTextArea();
        saveKeywordsButton = new javax.swing.JButton();
        resaveErrorHandlingFilesDialog = new javax.swing.JDialog();
        jScrollPane3 = new javax.swing.JScrollPane();
        resaveFileJList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        resaveErrorMessageTextArea = new javax.swing.JTextArea();
        missingErrorHandlingFilesDialog = new javax.swing.JDialog();
        jScrollPane5 = new javax.swing.JScrollPane();
        missingFileJList = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        missingErrorMessageTextArea = new javax.swing.JTextArea();
        otherErrorHandlingFilesDialog = new javax.swing.JDialog();
        jScrollPane7 = new javax.swing.JScrollPane();
        otherFileJList = new javax.swing.JList();
        jScrollPane8 = new javax.swing.JScrollPane();
        otherErrorMessageTextArea = new javax.swing.JTextArea();
        TabbedPane = new javax.swing.JTabbedPane();
        welcomePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        welcomeTextArea = new javax.swing.JTextArea();
        FileExplorerTreePane = new javax.swing.JScrollPane();
        FileExplorerTree = new javax.swing.JTree();
        TagTreePane = new javax.swing.JScrollPane();
        TagTree = new javax.swing.JTree();
        FileTreePane = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        statusMessageScrollPane = new javax.swing.JScrollPane();
        statusList = new javax.swing.JList();
        PrimaryActionPanel = new javax.swing.JPanel();
        resumeButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        customActionPane = new javax.swing.JPanel();
        TagListItemScrollPane = new javax.swing.JScrollPane();
        TagItemSearchList = new javax.swing.JList();
        SearchKeywordScrollPane = new javax.swing.JScrollPane();
        SearchKeyWordsTextArea = new javax.swing.JTextArea();
        PerformSearchButton = new javax.swing.JButton();
        SearchResultsListScrollPane = new javax.swing.JScrollPane();
        searchResultsList = new javax.swing.JList();
        searchSlider = new javax.swing.JSlider();
        progressBar = new javax.swing.JProgressBar();
        MenuBar = new javax.swing.JMenuBar();
        SystemMenu = new javax.swing.JMenu();
        openHelpDocumentMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        changeRepositoryFolderMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        cleanDataFilesMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        safelyExitSystem = new javax.swing.JMenuItem();
        TagsMenu = new javax.swing.JMenu();
        automaticallyTagFilesMenuItem = new javax.swing.JMenuItem();
        IndexingMenu = new javax.swing.JMenu();
        indexAllFilesMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        indexNewFilesMenuItem = new javax.swing.JMenuItem();

        modifyKeywordsButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        modifyKeywordsButton.setText("Modify Tag Keywords");
        modifyKeywordsButton.setActionCommand("");
        modifyKeywordsButton.setAlignmentX(0.5F);
        modifyKeywordsButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        modifyKeywordsButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                modifyKeywordsButtonActionPerformed(evt);
            }
        });

        removeTagFromFileButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        removeTagFromFileButton.setText("Remove Tag(s) from File");
        removeTagFromFileButton.setToolTipText("Removes the selected tags from the selected files");
        removeTagFromFileButton.setActionCommand("");
        removeTagFromFileButton.setAlignmentX(0.5F);
        removeTagFromFileButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        removeTagFromFileButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removeTagFromFileButtonActionPerformed(evt);
            }
        });

        removeFileFromTagButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        removeFileFromTagButton.setText("Remove File(s) from Tag");
        removeFileFromTagButton.setToolTipText("Removes the selected files from the selected tags");
        removeFileFromTagButton.setActionCommand("");
        removeFileFromTagButton.setAlignmentX(0.5F);
        removeFileFromTagButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        removeFileFromTagButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removeFileFromTagButtonActionPerformed(evt);
            }
        });

        addTagToFileButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        addTagToFileButton.setText("Add Tag(s) to File");
        addTagToFileButton.setToolTipText("Adds the selected tags to the selected files");
        addTagToFileButton.setActionCommand("");
        addTagToFileButton.setAlignmentX(0.5F);
        addTagToFileButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        addTagToFileButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addTagToFileButtonActionPerformed(evt);
            }
        });

        addFileToSystemButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        addFileToSystemButton.setText("Add File(s) to System");
        addFileToSystemButton.setToolTipText("Prompts the user for files to add to the system");
        addFileToSystemButton.setActionCommand("");
        addFileToSystemButton.setAlignmentX(0.5F);
        addFileToSystemButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        addFileToTagButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        addFileToTagButton.setText("Add File(s) to Tag");
        addFileToTagButton.setToolTipText("Adds the selected files to the selected tags");
        addFileToTagButton.setActionCommand("");
        addFileToTagButton.setAlignmentX(0.5F);
        addFileToTagButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        addFileToTagButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        addFileToTagButton.getAccessibleContext().setAccessibleDescription("");

        newTagButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        newTagButton.setText("Create New Tag");
        newTagButton.setToolTipText("Creates a new tag for the system");
        newTagButton.setActionCommand("");
        newTagButton.setAlignmentX(0.5F);
        newTagButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        newTagButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        newTagButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newTagButtonActionPerformed(evt);
            }
        });
        newTagButton.getAccessibleContext().setAccessibleDescription("");

        SearchResultsPopupMenu.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N

        OpenFilePopupMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        OpenFilePopupMenuItem.setText("Open/Launch File");
        OpenFilePopupMenuItem.setToolTipText("Opens each selected file with the system's default application");
        OpenFilePopupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                OpenFilePopupMenuItemActionPerformed(evt);
            }
        });
        SearchResultsPopupMenu.add(OpenFilePopupMenuItem);

        RootFolderPopupMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        RootFolderPopupMenuItem.setText("Open File's Parent Folder");
        RootFolderPopupMenuItem.setToolTipText("Open's the root folder in Windows Explorer for each selected file");
        RootFolderPopupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                RootFolderPopupMenuItemActionPerformed(evt);
            }
        });
        SearchResultsPopupMenu.add(RootFolderPopupMenuItem);
        SearchResultsPopupMenu.add(jSeparator4);

        AddTagFilePopupMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        AddTagFilePopupMenuItem.setText("Tag Selected File(s)");
        AddTagFilePopupMenuItem.setToolTipText("Tags the selected files with the tags selected under \"" + this.tagSearchListName + "\" window");
        AddTagFilePopupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                AddTagFilePopupMenuItemActionPerformed(evt);
            }
        });
        SearchResultsPopupMenu.add(AddTagFilePopupMenuItem);

        RemoveTagFilePopupMenuItem.setText("jMenuItem1");
        SearchResultsPopupMenu.add(RemoveTagFilePopupMenuItem);

        deleteTagFromSystemButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        deleteTagFromSystemButton.setText("Delete Tag From System");
        deleteTagFromSystemButton.setToolTipText("Removes all instances of a tag from the system");
        deleteTagFromSystemButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        deleteTagFromSystemButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteTagFromSystemButtonActionPerformed(evt);
            }
        });

        newTagDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        newTagDialog.setTitle("Add New Tag to Research Knowledge Manager");
        newTagDialog.setAlwaysOnTop(true);
        newTagDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        newTagDialog.setMinimumSize(newTagDialog.getPreferredSize());
        newTagDialog.setModal(true);
        newTagDialog.setType(java.awt.Window.Type.UTILITY);
        newTagDialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                newTagDialogWindowClosing(evt);
            }
        });

        addTagDialogButton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        addTagDialogButton.setText("Create Tag");
        addTagDialogButton.setToolTipText("Create the specified tag to the system");
        addTagDialogButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        addTagDialogButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addTagDialogButtonActionPerformed(evt);
            }
        });

        quitTagDialogButton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        quitTagDialogButton.setText("Quit");
        quitTagDialogButton.setToolTipText("Quit and do not create a new tag");
        quitTagDialogButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        quitTagDialogButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                quitTagDialogButtonActionPerformed(evt);
            }
        });

        userInputTagDialogTextField.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        userInputTagDialogTextField.setToolTipText("Name of the new tag to add to the system");
        userInputTagDialogTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        instructionsTagDialogLabel.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        instructionsTagDialogLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instructionsTagDialogLabel.setText("Type in the name of the tag you wish to add to the system");
        instructionsTagDialogLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        javax.swing.GroupLayout newTagDialogLayout = new javax.swing.GroupLayout(newTagDialog.getContentPane());
        newTagDialog.getContentPane().setLayout(newTagDialogLayout);
        newTagDialogLayout.setHorizontalGroup(
            newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newTagDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userInputTagDialogTextField)
                    .addGroup(newTagDialogLayout.createSequentialGroup()
                        .addComponent(addTagDialogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(quitTagDialogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(instructionsTagDialogLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                .addContainerGap())
        );
        newTagDialogLayout.setVerticalGroup(
            newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newTagDialogLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(instructionsTagDialogLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userInputTagDialogTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addTagDialogButton)
                    .addComponent(quitTagDialogButton))
                .addContainerGap())
        );

        this.newTagDialog.setSize(this.newTagDialog.getPreferredSize());

        confirmTagDeleteDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        confirmTagDeleteDialog.setTitle("Tag Deletion Confirmation");
        confirmTagDeleteDialog.setFont(this.getFont());
        confirmTagDeleteDialog.setMinimumSize(new java.awt.Dimension(466, 162));
        confirmTagDeleteDialog.setModal(true);
        confirmTagDeleteDialog.setType(java.awt.Window.Type.UTILITY);

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        jTextPane1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextPane1.setText("Are you sure you want to delete this tag?\nThis action will remove all instances of the tag from the system! \nThis action is irreversible!");
        jTextPane1.setAutoscrolls(false);
        jTextPane1.setOpaque(false);
        jScrollPane2.setViewportView(jTextPane1);

        confirmDeleteTagButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        confirmDeleteTagButton.setText("Delete Tag");
        confirmDeleteTagButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                confirmDeleteTagButtonActionPerformed(evt);
            }
        });

        cancelDeleteTagButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        cancelDeleteTagButton.setText("Cancel");
        cancelDeleteTagButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelDeleteTagButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout confirmTagDeleteDialogLayout = new javax.swing.GroupLayout(confirmTagDeleteDialog.getContentPane());
        confirmTagDeleteDialog.getContentPane().setLayout(confirmTagDeleteDialogLayout);
        confirmTagDeleteDialogLayout.setHorizontalGroup(
            confirmTagDeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmTagDeleteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(confirmTagDeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, confirmTagDeleteDialogLayout.createSequentialGroup()
                        .addComponent(confirmDeleteTagButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelDeleteTagButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        confirmTagDeleteDialogLayout.setVerticalGroup(
            confirmTagDeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmTagDeleteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(confirmTagDeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmDeleteTagButton)
                    .addComponent(cancelDeleteTagButton))
                .addContainerGap())
        );

        autoTagDialog.setAlwaysOnTop(true);
        autoTagDialog.setFocusableWindowState(false);
        autoTagDialog.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        autoTagDialog.setModal(true);
        autoTagDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        autoTagDialog.setType(java.awt.Window.Type.UTILITY);
        autoTagDialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                autoTagDialogWindowClosing(evt);
            }
        });

        TagListItemScrollPane1.setBorder(SearchKeywordScrollPane.getBorder());
        TagListItemScrollPane1.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), this.tagSearchListName, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        TagListAutoTag.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        TagListAutoTag.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        TagListAutoTag.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TagListAutoTag.setToolTipText("The list of tags currently in the system. You can select or deselect multiple tags by holding \"Shift\" or \"Control\"");
        TagListAutoTag.setFocusCycleRoot(true);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, TagItemSearchList, org.jdesktop.beansbinding.ELProperty.create("${model}"), TagListAutoTag, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        TagListAutoTag.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                TagListAutoTagValueChanged(evt);
            }
        });
        TagListItemScrollPane1.setViewportView(TagListAutoTag);

        sensitivitySlider.setFont(new java.awt.Font("Verdana", 2, 10)); // NOI18N
        sensitivitySlider.setMajorTickSpacing(50);
        sensitivitySlider.setMinorTickSpacing(10);
        sensitivitySlider.setPaintTicks(true);
        sensitivitySlider.setSnapToTicks(true);
        sensitivitySlider.setToolTipText("The sensitivity of the search. If the set of keywords have a certain density in the document, the document will be associated with the selected tag");
        sensitivitySlider.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Search Sensitivity", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        autoTagSubmit.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        autoTagSubmit.setText("Search");
        autoTagSubmit.setEnabled(false);
        autoTagSubmit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                autoTagSubmitActionPerformed(evt);
            }
        });

        tagKeyWordScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        tagKeyWordScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Tag Keywords", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        tagKeywordList.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        tagKeywordList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        tagKeywordList.setModel(new javax.swing.AbstractListModel()
        {
            String[] strings = { "No tag selected!" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        tagKeywordList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tagKeywordList.setFocusable(false);
        tagKeywordList.setRequestFocusEnabled(false);
        tagKeywordList.setValueIsAdjusting(true);
        tagKeywordList.setVerifyInputWhenFocusTarget(false);
        tagKeywordList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                tagKeywordListValueChanged(evt);
            }
        });
        tagKeyWordScrollPane.setViewportView(tagKeywordList);

        javax.swing.GroupLayout autoTagDialogLayout = new javax.swing.GroupLayout(autoTagDialog.getContentPane());
        autoTagDialog.getContentPane().setLayout(autoTagDialogLayout);
        autoTagDialogLayout.setHorizontalGroup(
            autoTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(autoTagDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(autoTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sensitivitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(autoTagSubmit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(autoTagDialogLayout.createSequentialGroup()
                        .addComponent(TagListItemScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(tagKeyWordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)))
                .addContainerGap())
        );
        autoTagDialogLayout.setVerticalGroup(
            autoTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(autoTagDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(autoTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TagListItemScrollPane1)
                    .addComponent(tagKeyWordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sensitivitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoTagSubmit)
                .addContainerGap())
        );

        Hashtable sliderLabels = new Hashtable();
        for(int i = 0; i <= this.sensitivitySlider.getMaximum(); i += this.sensitivitySlider.getMinorTickSpacing())
        {
            sliderLabels.putIfAbsent(i, new JLabel(((float)i/10) + "%"));
        }

        this.sensitivitySlider.setLabelTable(sliderLabels);
        this.sensitivitySlider.setPaintLabels(true);

        this.autoTagDialog.setSize(this.autoTagDialog.getPreferredSize());

        modifyKeywordsDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        modifyKeywordsDialog.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        modifyKeywordsDialog.setMinimumSize(modifyKeywordsDialog.getPreferredSize());
        modifyKeywordsDialog.setModal(true);
        modifyKeywordsDialog.setType(java.awt.Window.Type.UTILITY);

        modifyKeywordScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        modifyKeywordScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Tag Keywords", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        modifyKeywordsTextArea.setColumns(20);
        modifyKeywordsTextArea.setRows(5);
        modifyKeywordsTextArea.setWrapStyleWord(true);
        modifyKeywordsTextArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        modifyKeywordScrollPane.setViewportView(modifyKeywordsTextArea);

        saveKeywordsButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        saveKeywordsButton.setText("Save Keywords");
        saveKeywordsButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        saveKeywordsButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveKeywordsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout modifyKeywordsDialogLayout = new javax.swing.GroupLayout(modifyKeywordsDialog.getContentPane());
        modifyKeywordsDialog.getContentPane().setLayout(modifyKeywordsDialogLayout);
        modifyKeywordsDialogLayout.setHorizontalGroup(
            modifyKeywordsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyKeywordsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modifyKeywordsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveKeywordsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(modifyKeywordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );
        modifyKeywordsDialogLayout.setVerticalGroup(
            modifyKeywordsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyKeywordsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modifyKeywordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveKeywordsButton)
                .addContainerGap())
        );

        this.modifyKeywordsDialog.setSize(this.modifyKeywordsDialog.getPreferredSize().width,this.modifyKeywordsDialog.getPreferredSize().height + 40);

        resaveErrorHandlingFilesDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        resaveErrorHandlingFilesDialog.setTitle("Potential File Formatting Issues!");
        resaveErrorHandlingFilesDialog.setType(java.awt.Window.Type.POPUP);

        resaveFileJList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jScrollPane3.setViewportView(resaveFileJList);

        resaveErrorMessageTextArea.setEditable(false);
        resaveErrorMessageTextArea.setColumns(20);
        resaveErrorMessageTextArea.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        resaveErrorMessageTextArea.setLineWrap(true);
        resaveErrorMessageTextArea.setRows(5);
        resaveErrorMessageTextArea.setToolTipText("");
        resaveErrorMessageTextArea.setWrapStyleWord(true);
        resaveErrorMessageTextArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        resaveErrorMessageTextArea.setOpaque(false);
        jScrollPane4.setViewportView(resaveErrorMessageTextArea);

        javax.swing.GroupLayout resaveErrorHandlingFilesDialogLayout = new javax.swing.GroupLayout(resaveErrorHandlingFilesDialog.getContentPane());
        resaveErrorHandlingFilesDialog.getContentPane().setLayout(resaveErrorHandlingFilesDialogLayout);
        resaveErrorHandlingFilesDialogLayout.setHorizontalGroup(
            resaveErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resaveErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resaveErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        resaveErrorHandlingFilesDialogLayout.setVerticalGroup(
            resaveErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resaveErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.resaveErrorHandlingFilesDialog.setSize(this.resaveErrorHandlingFilesDialog.getPreferredSize());

        missingErrorHandlingFilesDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        missingErrorHandlingFilesDialog.setTitle("Missing Files Detected!");
        missingErrorHandlingFilesDialog.setType(java.awt.Window.Type.POPUP);

        missingFileJList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jScrollPane5.setViewportView(missingFileJList);

        missingErrorMessageTextArea.setEditable(false);
        missingErrorMessageTextArea.setColumns(20);
        missingErrorMessageTextArea.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        missingErrorMessageTextArea.setLineWrap(true);
        missingErrorMessageTextArea.setRows(5);
        missingErrorMessageTextArea.setToolTipText("");
        missingErrorMessageTextArea.setWrapStyleWord(true);
        missingErrorMessageTextArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        missingErrorMessageTextArea.setOpaque(false);
        jScrollPane6.setViewportView(missingErrorMessageTextArea);

        javax.swing.GroupLayout missingErrorHandlingFilesDialogLayout = new javax.swing.GroupLayout(missingErrorHandlingFilesDialog.getContentPane());
        missingErrorHandlingFilesDialog.getContentPane().setLayout(missingErrorHandlingFilesDialogLayout);
        missingErrorHandlingFilesDialogLayout.setHorizontalGroup(
            missingErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(missingErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(missingErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        missingErrorHandlingFilesDialogLayout.setVerticalGroup(
            missingErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, missingErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.missingErrorHandlingFilesDialog.setSize(this.missingErrorHandlingFilesDialog.getPreferredSize());

        otherErrorHandlingFilesDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        otherErrorHandlingFilesDialog.setTitle("Unknown Errors Detected!");
        otherErrorHandlingFilesDialog.setType(java.awt.Window.Type.POPUP);

        otherFileJList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jScrollPane7.setViewportView(otherFileJList);

        otherErrorMessageTextArea.setEditable(false);
        otherErrorMessageTextArea.setColumns(20);
        otherErrorMessageTextArea.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        otherErrorMessageTextArea.setLineWrap(true);
        otherErrorMessageTextArea.setRows(5);
        otherErrorMessageTextArea.setToolTipText("");
        otherErrorMessageTextArea.setWrapStyleWord(true);
        otherErrorMessageTextArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        otherErrorMessageTextArea.setOpaque(false);
        jScrollPane8.setViewportView(otherErrorMessageTextArea);

        javax.swing.GroupLayout otherErrorHandlingFilesDialogLayout = new javax.swing.GroupLayout(otherErrorHandlingFilesDialog.getContentPane());
        otherErrorHandlingFilesDialog.getContentPane().setLayout(otherErrorHandlingFilesDialogLayout);
        otherErrorHandlingFilesDialogLayout.setHorizontalGroup(
            otherErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, otherErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );
        otherErrorHandlingFilesDialogLayout.setVerticalGroup(
            otherErrorHandlingFilesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, otherErrorHandlingFilesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.otherErrorHandlingFilesDialog.setSize(this.otherErrorHandlingFilesDialog.getPreferredSize());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Research Knowledge Manager - SE Senior Design UTD Fall 2015");
        setBackground(new java.awt.Color(153, 153, 153));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setEnabled(false);
        setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        setMaximumSize(new java.awt.Dimension(0, 0));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener()
        {
            public void windowGainedFocus(java.awt.event.WindowEvent evt)
            {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt)
            {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        TabbedPane.setDoubleBuffered(true);
        TabbedPane.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        TabbedPane.setInheritsPopupMenu(true);
        TabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                TabbedPaneStateChanged(evt);
            }
        });

        welcomePanel.setLayout(new java.awt.GridLayout(0, 1));

        welcomeTextArea.setEditable(false);
        welcomeTextArea.setBackground(new java.awt.Color(241, 241, 241));
        welcomeTextArea.setColumns(20);
        welcomeTextArea.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        welcomeTextArea.setLineWrap(true);
        welcomeTextArea.setRows(5);
        welcomeTextArea.setText("Welcome to the Research Knowledge Manager!\n\nThe Research Knowledge Manager assists with managing a file database. With the ResearchKnowledgeManager, you can associate tags with files and perform search results on the tags.\n\nIf have you any question's on how to use the Research Knowledge Manager, press \"F1\" or go to \"System\" > \"Open Instructional Manual\" menu options and the system's technical manual will open with your system's default PDF application");
        welcomeTextArea.setWrapStyleWord(true);
        welcomeTextArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        welcomeTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        welcomeTextArea.setMargin(new java.awt.Insets(8, 8, 8, 8));
        welcomeTextArea.setName(""); // NOI18N
        jScrollPane1.setViewportView(welcomeTextArea);

        welcomePanel.add(jScrollPane1);

        TabbedPane.addTab("Start", welcomePanel);

        FileExplorerTree.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        FileExplorerTree.setModel(new FileTreeModelTree(this.rm.repositoryFolder));
        FileExplorerTree.setVisibleRowCount(40);
        FileExplorerTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt)
            {
                FileExplorerTreeValueChanged(evt);
            }
        });
        FileExplorerTreePane.setViewportView(FileExplorerTree);

        TabbedPane.addTab("File Explorer Hierarchy", FileExplorerTreePane);

        TagTree.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        TagTree.setModel(new TagModelTree(this.rm.Tags));
        TagTree.setLargeModel(true);
        TagTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt)
            {
                TagTreeValueChanged(evt);
            }
        });
        TagTreePane.setViewportView(TagTree);

        TabbedPane.addTab("Tag Hierarchy", TagTreePane);

        FileTree.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        FileTree.setModel(new FileModelTree(this.rm.Files));
        FileTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt)
            {
                FileTreeValueChanged(evt);
            }
        });
        FileTreePane.setViewportView(FileTree);

        TabbedPane.addTab("File Hierarchy", FileTreePane);

        statusMessageScrollPane.setBackground(new java.awt.Color(204, 204, 204));
        statusMessageScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusMessageScrollPane.setAutoscrolls(true);

        statusList.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        statusList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        statusList.setModel(this.statusMessages);
        statusList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        statusList.setDropMode(javax.swing.DropMode.ON);
        statusList.setName("statusList"); // NOI18N
        statusList.setValueIsAdjusting(true);
        statusList.setVisibleRowCount(1);
        statusList.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                statusListKeyPressed(evt);
            }
        });
        statusMessageScrollPane.setViewportView(statusList);
        statusList.getAccessibleContext().setAccessibleName("statusList");

        PrimaryActionPanel.setBackground(new java.awt.Color(204, 204, 204));
        PrimaryActionPanel.setOpaque(false);
        java.awt.GridBagLayout PrimaryActionPanelLayout = new java.awt.GridBagLayout();
        PrimaryActionPanelLayout.columnWidths = new int[] {0, 5, 0, 5, 0};
        PrimaryActionPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0};
        PrimaryActionPanel.setLayout(PrimaryActionPanelLayout);

        resumeButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        resumeButton.setText("Resume Task");
        resumeButton.setActionCommand("");
        resumeButton.setAlignmentX(0.5F);
        resumeButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        resumeButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        resumeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                resumeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        PrimaryActionPanel.add(resumeButton, gridBagConstraints);
        resumeButton.getAccessibleContext().setAccessibleDescription("");

        pauseButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        pauseButton.setText("Pause Task");
        pauseButton.setActionCommand("");
        pauseButton.setAlignmentX(0.5F);
        pauseButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pauseButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        pauseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pauseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        PrimaryActionPanel.add(pauseButton, gridBagConstraints);
        pauseButton.getAccessibleContext().setAccessibleDescription("");

        cancelButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        cancelButton.setText("Cancel Task");
        cancelButton.setActionCommand("");
        cancelButton.setAlignmentX(0.5F);
        cancelButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        cancelButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        PrimaryActionPanel.add(cancelButton, gridBagConstraints);
        cancelButton.getAccessibleContext().setAccessibleDescription("");

        mainPanel.setBackground(new java.awt.Color(229, 229, 229));
        mainPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED)));

        customActionPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        customActionPane.setMinimumSize(new java.awt.Dimension(817, 28));
        customActionPane.setLayout(new java.awt.GridLayout(0, 2, 2, 2));

        TagListItemScrollPane.setBorder(SearchKeywordScrollPane.getBorder());
        TagListItemScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), this.tagSearchListName, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        TagItemSearchList.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        TagItemSearchList.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        TagItemSearchList.setModel(this.TagItemListModel);
        TagItemSearchList.setToolTipText("The list of tags currently in the system. You can select or deselect multiple tags by holding \"Shift\" or \"Control\"");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), TagItemSearchList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        TagListItemScrollPane.setViewportView(TagItemSearchList);

        SearchKeywordScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        SearchKeywordScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Keyword Query Terms", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        SearchKeyWordsTextArea.setColumns(25);
        SearchKeyWordsTextArea.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        SearchKeyWordsTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.white, null));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), SearchKeyWordsTextArea, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        SearchKeywordScrollPane.setViewportView(SearchKeyWordsTextArea);

        PerformSearchButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        PerformSearchButton.setText("Perform Search");
        PerformSearchButton.setToolTipText("Searches for files with the specified tags and/or keywords");
        PerformSearchButton.setActionCommand("");
        PerformSearchButton.setAlignmentX(0.5F);
        PerformSearchButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), PerformSearchButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PerformSearchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                PerformSearchButtonActionPerformed(evt);
            }
        });

        SearchResultsListScrollPane.setBorder(SearchKeywordScrollPane.getBorder());
        SearchResultsListScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), this.searchResultsName, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        searchResultsList.setBorder(TagItemSearchList.getBorder());
        searchResultsList.setFont(new java.awt.Font("Verdana", 2, 10)); // NOI18N
        searchResultsList.setModel(new DefaultListModel());
        searchResultsList.setToolTipText("The list of files returned by the most recent search query");
        searchResultsList.setComponentPopupMenu(SearchResultsPopupMenu);
        searchResultsList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                searchResultsListValueChanged(evt);
            }
        });
        SearchResultsListScrollPane.setViewportView(searchResultsList);

        searchSlider.setFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        searchSlider.setMajorTickSpacing(50);
        searchSlider.setMinorTickSpacing(10);
        searchSlider.setPaintTicks(true);
        searchSlider.setSnapToTicks(true);
        searchSlider.setToolTipText("The sensitivity of the search. If the set of keywords have a certain density in the document, the document will be associated with the selected tag");
        searchSlider.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Search Sensitivity", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(customActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(TagListItemScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(searchSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .addComponent(SearchKeywordScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .addComponent(PerformSearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(SearchResultsListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SearchResultsListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(SearchKeywordScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PerformSearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                    .addComponent(TagListItemScrollPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Hashtable standardSliderLabels = new Hashtable();
        for(int i = 0; i <= this.searchSlider.getMaximum(); i += this.searchSlider.getMinorTickSpacing())
        {
            JLabel buffer = new JLabel(((float)i/10) + "%");
            buffer.setFont(new Font("Verdana", Font.PLAIN, 8));
            standardSliderLabels.putIfAbsent(i, buffer );
        }

        this.searchSlider.setLabelTable(standardSliderLabels);
        this.searchSlider.setPaintLabels(true);

        progressBar.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        progressBar.setMaximum(0);
        progressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        progressBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        progressBar.setName(""); // NOI18N
        progressBar.setOpaque(false);
        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressBar.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                progressBarStateChanged(evt);
            }
        });

        MenuBar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N

        SystemMenu.setText("System");
        SystemMenu.setToolTipText("Contains menu options related to the system");
        SystemMenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        SystemMenu.addMenuListener(new javax.swing.event.MenuListener()
        {
            public void menuCanceled(javax.swing.event.MenuEvent evt)
            {
                SystemMenuMenuCanceled(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt)
            {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt)
            {
            }
        });

        openHelpDocumentMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        openHelpDocumentMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        openHelpDocumentMenuItem.setText("Open Instructional Manual");
        openHelpDocumentMenuItem.setToolTipText("Opens the Research Knowledge Manager's Instructional Manual");
        openHelpDocumentMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openHelpDocumentMenuItemActionPerformed(evt);
            }
        });
        SystemMenu.add(openHelpDocumentMenuItem);
        SystemMenu.add(jSeparator7);

        changeRepositoryFolderMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        changeRepositoryFolderMenuItem.setText("Change Repository Folder");
        changeRepositoryFolderMenuItem.setToolTipText("Changes the current repository folder to a specified folder");
        changeRepositoryFolderMenuItem.setActionCommand("askForFolder");
        changeRepositoryFolderMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                changeRepositoryFolderMenuItemActionPerformed(evt);
            }
        });
        SystemMenu.add(changeRepositoryFolderMenuItem);
        SystemMenu.add(jSeparator3);

        cleanDataFilesMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        cleanDataFilesMenuItem.setText("Clean Data Files");
        cleanDataFilesMenuItem.setToolTipText("Wipes all files used by the system for data storage. Use with extreme caution.");
        cleanDataFilesMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cleanDataFilesMenuItemActionPerformed(evt);
            }
        });
        SystemMenu.add(cleanDataFilesMenuItem);
        progressBar.setString("0%");

        SystemMenu.add(jSeparator6);

        safelyExitSystem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        safelyExitSystem.setText("Quit");
        safelyExitSystem.setToolTipText("Safely exits the system");
        safelyExitSystem.setActionCommand("Exit");
        safelyExitSystem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                safelyExitSystemActionPerformed(evt);
            }
        });
        SystemMenu.add(safelyExitSystem);

        MenuBar.add(SystemMenu);

        TagsMenu.setText("Tags");
        TagsMenu.setToolTipText("Contains options related to file tagging");
        TagsMenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        automaticallyTagFilesMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        automaticallyTagFilesMenuItem.setText("Automatically Tag Files");
        automaticallyTagFilesMenuItem.setToolTipText("Automatically Finds Files for a User Specified Tag");
        automaticallyTagFilesMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                automaticallyTagFilesMenuItemActionPerformed(evt);
            }
        });
        TagsMenu.add(automaticallyTagFilesMenuItem);

        MenuBar.add(TagsMenu);

        IndexingMenu.setText("Indexing");
        IndexingMenu.setToolTipText("Contains options related to system indexing");
        IndexingMenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        indexAllFilesMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        indexAllFilesMenuItem.setText("Index All Files");
        indexAllFilesMenuItem.setToolTipText("Creates an index of all files in the stored repository folder. Autotmatically tags files based on their file type");
        indexAllFilesMenuItem.setActionCommand("IndexAll");
        indexAllFilesMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                indexAllFilesMenuItemActionPerformed(evt);
            }
        });
        IndexingMenu.add(indexAllFilesMenuItem);
        IndexingMenu.add(jSeparator8);

        indexNewFilesMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        indexNewFilesMenuItem.setText("Index New Files");
        indexNewFilesMenuItem.setToolTipText("Creates an index of all new files in the stored repository folder. Autotmatically tags files based on their file type");
        indexNewFilesMenuItem.setActionCommand("IndexNew");
        indexNewFilesMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                indexNewFilesMenuItemActionPerformed(evt);
            }
        });
        IndexingMenu.add(indexNewFilesMenuItem);

        MenuBar.add(IndexingMenu);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(statusMessageScrollPane)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(PrimaryActionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 1250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PrimaryActionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("Research Knowledge Manager - SE Senior Design");

        bindingGroup.bind();

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void SystemMenuMenuCanceled(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_SystemMenuMenuCanceled
        // TODO add your handling code here:
    }//GEN-LAST:event_SystemMenuMenuCanceled

    private void changeRepositoryFolderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRepositoryFolderMenuItemActionPerformed
        try
        {
            if (rm.askForRepository())
            {
                this.indexAllFilesMenuItemActionPerformed(null);
                this.autoIndexAll();
            }
        }
        catch (IOException ex)
        {
            System.err.println("I/O Error!");
        }
    }//GEN-LAST:event_changeRepositoryFolderMenuItemActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        if (executingThread != null)
        {

            synchronized (executingThread)
            {
                if (rm.actionStatus == ResearchKnowledgeManager.activeState.ACTIVE)
                {
                    rm.setState(ResearchKnowledgeManager.activeState.PAUSED);
                    newMessage("Pausing current action. Click \"Resume Action\" to resume the action");
                }
            }
        }
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void cleanDataFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanDataFilesMenuItemActionPerformed
        exitNoWrite = true;

        ConfirmationDialog confirm = new ConfirmationDialog(this, true);
        confirm.setVisible(true);
        if (confirm.getReturnStatus() == 1)
        {
            this.rm.clean();
            progressBar.setMaximum(0);

            // Series of actions disabling menu items to prevent unexpected exceptions from user input
            // These enable actions only matter if the application DOES NOT exit
            this.indexAllFilesMenuItem.setEnabled(false);
            this.indexNewFilesMenuItem.setEnabled(false);
            this.changeRepositoryFolderMenuItem.setEnabled(false);
            this.automaticallyTagFilesMenuItem.setEnabled(false);

            // Closes the program
            this.formWindowClosing(null);
        }

    }//GEN-LAST:event_cleanDataFilesMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        if (!exitNoWrite)
        {
            this.rm.exit();
        }
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void safelyExitSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_safelyExitSystemActionPerformed
        this.formWindowClosing(null);
    }//GEN-LAST:event_safelyExitSystemActionPerformed

    private void resumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeButtonActionPerformed

        if (executingThread != null)
        {
            synchronized (executingThread)
            {
                if (rm.actionStatus == ResearchKnowledgeManager.activeState.PAUSED)
                {
                    executingThread.notifyAll();
                    rm.setState(ResearchKnowledgeManager.activeState.ACTIVE);
                    newMessage("Resuming current action...");
                }
            }
        }
    }//GEN-LAST:event_resumeButtonActionPerformed

    private void indexAllFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexAllFilesMenuItemActionPerformed

        if (this.executingThread == null)
        {
            this.executingThread = new Thread(() ->
            {

                //Thread initializations
                progressBar.setMaximum(0);
                rm.setState(ResearchKnowledgeManager.activeState.ACTIVE);
                newMessage("Preparing some calculations, please wait...");
                progressBar.setMaximum(rm.fileIndexer.computeMaxFolderChild(rm.repositoryFolder));

                newMessage("Calculations completed! File indexing will now begin. Please wait for confirmation message...");
                rm.fileIndexer.indexAllReturn = rm.fileIndexer.indexFilesAll(rm.repositoryFolder, rm);

                this.rm.sortFileClass(this.rm.Files);
                this.rm.sortTagClass(this.rm.Tags);

                this.refreshTagItemList();

                newMessage(rm.lineSeparator);
                rm.fileIndexer.saveIndexAll(rm.dataDirectory);

                // Thread cleanup
                this.updateFileTree();
                this.updateTagTree();
                newMessage("Finished processing all files!");
                newMessage(this.rm.lineSeparator);
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                this.progressBar.setString("Task completed!");

                rm.updateLastModified();
                executingThread = null;
            });

            this.startTask();
        }

        else
        {
            newMessage("Could not prepare the system for the selected task! A task is already in execution!");
        }

    }//GEN-LAST:event_indexAllFilesMenuItemActionPerformed

    private void indexNewFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexNewFilesMenuItemActionPerformed

        if (this.executingThread == null)
        {
            this.executingThread = new Thread(() ->
            {

                //Thread initializations
                progressBar.setMaximum(0);
                rm.setState(ResearchKnowledgeManager.activeState.ACTIVE);
                newMessage("Preparing some calculations, please wait...");
                progressBar.setMaximum(rm.fileIndexer.computeMaxFolderChild(rm.repositoryFolder));
                newMessage("Calculations completed! File indexing will now begin. Please wait for confirmation message...");

                rm.fileIndexer.indexNewReturn = rm.fileIndexer.indexFilesNew(rm.repositoryFolder, rm);
                newMessage(rm.lineSeparator);

                rm.fileIndexer.saveIndexNew(rm.dataDirectory);

                // Thread cleanup
                this.rm.sortFileClass(this.rm.Files);
                this.rm.sortTagClass(this.rm.Tags);

                this.refreshTagItemList();

                this.updateFileTree();
                this.updateTagTree();

                newMessage("Finished processing new files!");
                newMessage(rm.lineSeparator);
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);

                this.progressBar.setString("Task completed!");
                rm.updateLastModified();
                executingThread = null;
            });

            this.startTask();
        }

        else
        {
            newMessage("Could not prepare the system for the selected task! A task is already in execution!");
        }

    }//GEN-LAST:event_indexNewFilesMenuItemActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus

    }//GEN-LAST:event_formWindowGainedFocus

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

        newMessage("Current action cancelled!");
        newMessage(rm.lineSeparator);
        progressBar.setString("0%");
        progressBar.setValue(0);
        progressBar.setMaximum(0);

        switch (rm.actionStatus)
        {
            case ACTIVE:
            {
                // May need to find better alternative to stop() method since the method is deprecated...
                executingThread.stop();
                executingThread = null;
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                break;
            }
            case PAUSED:
            {
                // May need to find better alternative to stop() method since the method is deprecated...
                executingThread.stop();
                executingThread = null;
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                break;
            }

        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void statusListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_statusListKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE)
        {
            statusMessages.removeAllElements();
            newMessage("Message log cleared!");
            newMessage(rm.lineSeparator);
        }
        else
        {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE && statusMessages.getSize() > 0)
            {
                statusMessages.remove(statusMessages.getSize() - 1);
            }
        }
    }//GEN-LAST:event_statusListKeyPressed

    private void newTagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTagButtonActionPerformed

        this.newTagDialog.setVisible(true);
    }//GEN-LAST:event_newTagButtonActionPerformed

    private void removeFileFromTagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileFromTagButtonActionPerformed
        TreePath[] possiblePaths = TagTree.getSelectionPaths();
        newMessage("Beginning removal process...");
        for (TreePath path : possiblePaths)
        {
            Object[] wholePath = path.getPath();
            if (wholePath.length == 3)
            {
                ((TagClass) wholePath[1]).removeFile((String) wholePath[2], true);
                newMessage((String) wholePath[2] + " successfully removed from  tag \"" + ((TagClass) wholePath[1]).toString() + "\"");

                this.updateFileTree();
                this.updateTagTree();
            }

            else
            {
                newMessage("Removal process could not be completed for\t" + wholePath[wholePath.length - 1]);
            }
        }

        newMessage("Removal process completed!");
        newMessage(rm.lineSeparator);

    }//GEN-LAST:event_removeFileFromTagButtonActionPerformed

    private void addTagToFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTagToFileButtonActionPerformed

        List<TagClass> selectedValues = this.TagItemSearchList.getSelectedValuesList();

        newMessage("Beginning tagging process...");

        TreePath[] possiblePaths = FileTree.getSelectionPaths();
        if (!selectedValues.isEmpty())
        {
            // Adds selected tags to the selected files

            for (int h = 0; h < possiblePaths.length && possiblePaths[h].getPath().length == 2; h++)
            {
                FileClass fileItem = (FileClass) possiblePaths[h].getPath()[1];
                for (TagClass selectedValue : selectedValues)
                {
                    fileItem.addTag(selectedValue.toString());
                    fileItem.associatedTags.sort(null);
                }
                this.updateFileTree();
                this.updateTagTree();

                newMessage("Tagging process completed for " + fileItem);
            }
        }
        else
        {
            newMessage("No tags chosen! Please select tags from the \"" + this.tagSearchListName + "\" list!");
            newMessage("Tagging process failed!");
        }

        FileTree.getSelectionModel().clearSelection();
    }//GEN-LAST:event_addTagToFileButtonActionPerformed

    private void PerformSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PerformSearchButtonActionPerformed

        List<TagClass> selectedTags = this.TagItemSearchList.getSelectedValuesList();

        if (selectedTags.isEmpty())
        {
            newMessage("Search cannot be completed! No tag terms were selected.");
            newMessage(rm.lineSeparator);
            return;
        }

        else if (this.executingThread == null)
        {
            this.executingThread = new Thread(() ->
            {
                rm.setState(ResearchKnowledgeManager.activeState.ACTIVE);

                if (this.SearchKeyWordsTextArea.getText().trim().isEmpty())
                {
                    newMessage("No keywords selected! Performing search without keywords");
                    this.SearchKeyWordsTextArea.setText(null);
                }

                Vector<FileClass> tagResults = new Vector<>();

                newMessage("Performing Tag-based Search...");

                Vector<FileClass> intermediateResults = rm.findTagsComplete(selectedTags);

                if (!this.SearchKeyWordsTextArea.getText().trim().isEmpty())
                {
                    ProcessFileTest tagTester = new ProcessFileTest(this.rm);
                    String[] sbuffer = this.SearchKeyWordsTextArea.getText().split(",");
                    Vector<String> confirmedKeywords = new Vector<>(5);

                    // Parsing through for the keywords
                    for (String s : sbuffer)
                    {
                        if (!s.isEmpty())
                        {
                            confirmedKeywords.add(s.trim());
                        }

                    }

                    this.progressBar.setValue(0);
                    this.progressBar.setMaximum(intermediateResults.size());

                    Vector<FileClass> reSaveFiles = new Vector<>(2);
                    Vector<FileClass> missingFiles = new Vector<>(2);
                    Vector<FileClass> otherBadFiles = new Vector<>(2);

                    for (FileClass fc : intermediateResults)
                    {
                        this.progressBar.setValue(this.progressBar.getValue() + 1);
                        this.progressBar.setString((int) ((float) 100 * this.progressBar.getValue() / this.progressBar.getMaximum()) + "%");

                        if (tagTester.isProcessableFile(fc))
                        {
                            try
                            {
                                String documentContent = tagTester.getFileContent(fc);

                                if (documentContent != null && tagTester.TagSearch(confirmedKeywords, documentContent, this.searchSlider.getValue()))
                                {
                                    tagResults.add(fc);
                                }
                            }

                            catch (org.apache.poi.poifs.filesystem.NotOLE2FileException e)
                            {
                                //System.err.println("Found a word file that needs to be resaved: " + fc.toString());
                                reSaveFiles.add(fc);
                            }

                            catch (FileNotFoundException e)
                            {
                                //System.err.println("Found a file that is missing: " + fc.toString());
                                missingFiles.add(fc);
                            }

                            catch (org.apache.poi.poifs.filesystem.OfficeXmlFileException e)
                            {
                                //System.err.println("Found a XML file that needs to be resaved:" + fc.toString());
                                reSaveFiles.add(fc);
                            }

                            catch (Exception e)
                            {
                                otherBadFiles.add(fc);
                                System.err.println("A file with an undetermined exception was found! " + fc.toString());
                                System.err.println(e.getClass());
                            }
                        }

                        else
                        {
                            // newMessage(fc.toString() + " is currently not processable by the system. Removing the file from the result list");
                        }

                    }

                    if (!reSaveFiles.isEmpty())
                    {
                        ErrorHandleFunction(reSaveFiles, UserInterface.ErrorType.RESAVE);
                    }

                    if (!missingFiles.isEmpty())
                    {
                        ErrorHandleFunction(missingFiles, UserInterface.ErrorType.MISSING);
                    }

                    if (!otherBadFiles.isEmpty())
                    {
                        ErrorHandleFunction(otherBadFiles, UserInterface.ErrorType.OTHER);
                    }
                }

                else
                {
                    tagResults = intermediateResults;
                }

                DefaultListModel buffer = new DefaultListModel();

                for (FileClass tagResult : tagResults)
                {
                    buffer.addElement(tagResult);
                }
                updateSearchResultsName(buffer.size());

                this.searchResultsList.setModel(buffer);

                newMessage("Tag search completed! Results are displayed in \"Search Results\"");
                newMessage(rm.lineSeparator);
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                this.progressBar.setString("Task completed!");

                executingThread = null;
            });

            this.startTask();
        }

        else
        {
            newMessage("Could not prepare the system for the selected task! A task is already in execution!");
        }
    }//GEN-LAST:event_PerformSearchButtonActionPerformed

    private void searchResultsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_searchResultsListValueChanged

    }//GEN-LAST:event_searchResultsListValueChanged

    private void removeTagFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTagFromFileButtonActionPerformed
        List<TagClass> selectedValues = this.TagItemSearchList.getSelectedValuesList();

        TreePath[] buffer = FileTree.getSelectionPaths();

        newMessage("Beginning removal process...");
        if (!selectedValues.isEmpty())
        {
            // Adds selected tags to the selected files
            for (TreePath tp : buffer)
            {
                for (int i = 0; i < selectedValues.size() && tp.getPathCount() == 2; i++)
                {
                    ((FileClass) tp.getLastPathComponent()).removeTag(selectedValues.get(i).toString(), true);
                }
                this.updateFileTree();
                this.updateTagTree();
                newMessage("Removal process completed!");

            }
        }
        else
        {
            newMessage("No tags chosen. Please select tags from the \"Tag Query Terms\" list!");
        }

        newMessage(rm.lineSeparator);
    }//GEN-LAST:event_removeTagFromFileButtonActionPerformed

    private void OpenFilePopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenFilePopupMenuItemActionPerformed
        List<FileClass> selectedItems = this.searchResultsList.getSelectedValuesList();

        if (!selectedItems.isEmpty())
        {
            try
            {
                for (FileClass selectedItem : selectedItems)
                {
                    newMessage("Launching the following file: " + selectedItem.toString());
                    Desktop.getDesktop().open(new File(selectedItem.toString()));
                }
            }
            catch (IOException ex)
            {
                System.err.println("IO EXCEPTION!" + ex);
            }
        }
        else
        {
            newMessage("No files listed as results! Please do a search first!");
        }

        newMessage(this.rm.lineSeparator);
    }//GEN-LAST:event_OpenFilePopupMenuItemActionPerformed

    private void RootFolderPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RootFolderPopupMenuItemActionPerformed
        List<FileClass> selectedItems = this.searchResultsList.getSelectedValuesList();

        if (!selectedItems.isEmpty())
        {
            try
            {
                for (FileClass selectedItem : selectedItems)
                {
                    File buffer = new File(selectedItem.toString());
                    newMessage("Launching the following folder: " + buffer.getParent());
                    Desktop.getDesktop().open(new File(buffer.getParent()));
                }
            }
            catch (IOException ex)
            {
                System.err.println("IO EXCEPTION!" + ex);
            }
        }
        else
        {
            newMessage("No files listed as results! Please do a search first!");
        }

        newMessage(this.rm.lineSeparator);
    }//GEN-LAST:event_RootFolderPopupMenuItemActionPerformed

    private void TabbedPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_TabbedPaneStateChanged
    {//GEN-HEADEREND:event_TabbedPaneStateChanged
        this.customActionPane.removeAll();

        if (TabbedPane.getSelectedComponent() == this.TagTreePane)
        {

            this.customActionPane.add(this.newTagButton);

            // Might not be needed for usability
            // this.customActionPane.add(this.addFileToTagButton);
            this.customActionPane.add(this.modifyKeywordsButton);
            this.customActionPane.add(this.removeFileFromTagButton);
            this.customActionPane.add(this.deleteTagFromSystemButton);

            // Sets the default state of the buttons to false
            this.removeFileFromTagButton.setEnabled(false);
            this.addFileToTagButton.setEnabled(false);
            this.modifyKeywordsButton.setEnabled(false);
            this.newTagButton.setEnabled(true);
            this.deleteTagFromSystemButton.setEnabled(false);

        }
        else
        {
            if (TabbedPane.getSelectedComponent() == this.FileTreePane)
            {

                // this.customActionPane.add(this.addFileToSystemButton);
                this.customActionPane.add(this.addTagToFileButton);
                this.customActionPane.add(this.removeTagFromFileButton);

                // Sets the default state of the buttons to false
                this.removeTagFromFileButton.setEnabled(false);
                //  this.addFileToSystemButton.setEnabled(false);
                this.addTagToFileButton.setEnabled(false);

            }
            else
            {
                if (TabbedPane.getSelectedComponent() == this.FileExplorerTreePane)
                {

                }
            }

        }
        this.repaint();
    }//GEN-LAST:event_TabbedPaneStateChanged

    private void FileExplorerTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_FileExplorerTreeValueChanged
    {//GEN-HEADEREND:event_FileExplorerTreeValueChanged

    }//GEN-LAST:event_FileExplorerTreeValueChanged

    private void FileTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_FileTreeValueChanged
    {//GEN-HEADEREND:event_FileTreeValueChanged
        int minPathCount = Integer.MAX_VALUE;
        int maxPathCount = Integer.MIN_VALUE;

        TreePath[] possiblePaths = FileTree.getSelectionPaths();

        if (possiblePaths == null)
        {
            TagTree.clearSelection();
            return;
        }
        for (TreePath tb : possiblePaths)
        {

            minPathCount = min(tb.getPathCount(), minPathCount);
            maxPathCount = max(tb.getPathCount(), maxPathCount);
        }

        // Current selection is on a node (tag name)
        if (minPathCount == 1)
        {
            this.removeTagFromFileButton.setEnabled(false);
            this.addFileToSystemButton.setEnabled(true);
            this.addTagToFileButton.setEnabled(false);
        }

        // Selection is on a leaf
        else
        {
            if (maxPathCount == 3)
            {
                this.removeTagFromFileButton.setEnabled(false);
                this.addFileToSystemButton.setEnabled(true);
                this.addTagToFileButton.setEnabled(false);

            }

            else
            {
                this.removeTagFromFileButton.setEnabled(true);
                this.addFileToSystemButton.setEnabled(true);
                this.addTagToFileButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_FileTreeValueChanged

    private void TagTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_TagTreeValueChanged
    {//GEN-HEADEREND:event_TagTreeValueChanged
        int minPath = Integer.MAX_VALUE;
        int maxPath = Integer.MIN_VALUE;

        TreePath[] possiblePaths = TagTree.getSelectionPaths();

        if (possiblePaths == null)
        {
            TagTree.clearSelection();
            return;
        }
        for (TreePath tb : possiblePaths)
        {
            minPath = min(tb.getPathCount(), minPath);
            maxPath = max(tb.getPathCount(), minPath);
        }
        // Current selection is on a node (tag name)
        if (minPath == 1)
        {
            this.removeFileFromTagButton.setEnabled(false);

            // Current selection is on a node (tag name)false);
            this.addFileToTagButton.setEnabled(false);
            this.modifyKeywordsButton.setEnabled(false);
            this.newTagButton.setEnabled(true);
        }

        // Selection is on a leaf
        else
        {
            if (maxPath == 3)
            {

                this.removeFileFromTagButton.setEnabled(true);
                this.addFileToTagButton.setEnabled(false);
                this.modifyKeywordsButton.setEnabled(false);
                this.newTagButton.setEnabled(true);

            }
            else
            {
                if (maxPath == 2)
                {
                    this.removeFileFromTagButton.setEnabled(false);
                    this.addFileToTagButton.setEnabled(true);

                    if (TagTree.getSelectionCount() == 1)
                    {
                        // Tags currently do not use keywords so this button will always be disabled
                        this.modifyKeywordsButton.setEnabled(true);
                        this.deleteTagFromSystemButton.setEnabled(true);
                    }

                    else
                    {
                        this.modifyKeywordsButton.setEnabled(false);
                        this.deleteTagFromSystemButton.setEnabled(false);
                    }

                    this.newTagButton.setEnabled(true);

                }
            }
        }
    }//GEN-LAST:event_TagTreeValueChanged

    private void AddTagFilePopupMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_AddTagFilePopupMenuItemActionPerformed
    {//GEN-HEADEREND:event_AddTagFilePopupMenuItemActionPerformed
        List<TagClass> selectedTagValues = this.TagItemSearchList.getSelectedValuesList();
        List<FileClass> selectedFileValues = this.searchResultsList.getSelectedValuesList();
        newMessage("Beginning tagging process...");

        if (!selectedTagValues.isEmpty() && !selectedFileValues.isEmpty())
        {
            // Adds selected tags to the selected files
            for (TagClass selectedTagValue : selectedTagValues)
            {
                // Adds a tag for each selected file
                for (FileClass selectedFileValue : selectedFileValues)
                {
                    selectedFileValue.addTag(selectedTagValue.toString());
                    selectedFileValue.associatedTags.sort(null);
                }
            }

            this.updateFileTree();
            this.updateTagTree();

            newMessage("Tagging process completed!");

        }
        else
        {
            if (selectedTagValues.isEmpty())
            {
                newMessage("No Tags chosen. Please select tags from the \"Tag Query Terms\" list!");

            }
            else
            {
                newMessage("No Files chosen. Please select files from the \"Search Results\" list1 ");
            }
            newMessage("Tagging failed!");

        }
        newMessage(rm.lineSeparator);
    }//GEN-LAST:event_AddTagFilePopupMenuItemActionPerformed

    private void addTagDialogButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addTagDialogButtonActionPerformed
    {//GEN-HEADEREND:event_addTagDialogButtonActionPerformed
        TagClass buffer = new TagClass(this.userInputTagDialogTextField.getText().trim().toLowerCase(), this.rm);

        boolean found = false;

        for (TagClass i : this.rm.Tags)
        {
            if (i.toString().equals(buffer.toString()))
            {
                found = true;
            }
        }

        if (found == false)
        {
            this.rm.addTagClass(buffer);

            newMessage("\"" + this.userInputTagDialogTextField.getText().trim().toLowerCase() + "\" has been successfully added as a tag to the system!");
            this.rm.Tags.sort(null);

            this.updateTagTree();
            this.refreshTagItemList();
        }

        else
        {
            if ("".equals(buffer.toString()))
            {
                newMessage("Please type something as the tag name!");
            }

            else
            {
                newMessage("Tag already exists in the system! Please choose another tag name");
            }
        }

        newMessage(this.rm.lineSeparator);
        this.userInputTagDialogTextField.setText("");
        this.newTagDialog.dispose();
    }//GEN-LAST:event_addTagDialogButtonActionPerformed

    private void quitTagDialogButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quitTagDialogButtonActionPerformed
    {//GEN-HEADEREND:event_quitTagDialogButtonActionPerformed
        this.userInputTagDialogTextField.setText("");
        this.newTagDialog.dispose();
    }//GEN-LAST:event_quitTagDialogButtonActionPerformed

    private void newTagDialogWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_newTagDialogWindowClosing
    {//GEN-HEADEREND:event_newTagDialogWindowClosing
        this.quitTagDialogButtonActionPerformed(null);
    }//GEN-LAST:event_newTagDialogWindowClosing

    private void deleteTagFromSystemButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteTagFromSystemButtonActionPerformed
    {//GEN-HEADEREND:event_deleteTagFromSystemButtonActionPerformed
        this.confirmTagDeleteDialog.setVisible(true);

        if (this.confirmDeleteTag)
        {
            TagClass buffer = (TagClass) (this.TagTree.getSelectionModel().getLeadSelectionPath().getLastPathComponent());

            if (buffer != null)
            {
                newMessage("Preparing to remove the following tag: \t" + buffer.toString());

                for (FileClass i : this.rm.Files)
                {
                    i.removeTag(buffer.toString(), true);
                }

                this.rm.Tags.remove(buffer);

                newMessage("The tag \"" + buffer.toString() + "\" has been removed from all files!");

                this.updateTagTree();
                this.updateFileTree();
                this.refreshTagItemList();
            }

            else
            {
                newMessage("No tags chosen! Please select tags from the \"" + this.tagSearchListName + "\" list!");
            }
        }

        else
        {
            newMessage("Tag deletion cancelled!");
        }

        this.confirmDeleteTag = false;
        newMessage(this.rm.lineSeparator);
    }//GEN-LAST:event_deleteTagFromSystemButtonActionPerformed

    private void confirmDeleteTagButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_confirmDeleteTagButtonActionPerformed
    {//GEN-HEADEREND:event_confirmDeleteTagButtonActionPerformed
        confirmDeleteTag = true;
        this.confirmTagDeleteDialog.dispose();
    }//GEN-LAST:event_confirmDeleteTagButtonActionPerformed

    private void cancelDeleteTagButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelDeleteTagButtonActionPerformed
    {//GEN-HEADEREND:event_cancelDeleteTagButtonActionPerformed
        this.confirmTagDeleteDialog.dispose();
    }//GEN-LAST:event_cancelDeleteTagButtonActionPerformed

    private void openHelpDocumentMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openHelpDocumentMenuItemActionPerformed
    {//GEN-HEADEREND:event_openHelpDocumentMenuItemActionPerformed

        try
        {
            File buffer = new File("./Research Knowledge Manager Instructional Manual.pdf");

            if (buffer.exists())
            {
                Desktop.getDesktop().open(buffer);
            }
            else
            {
                newMessage("Instructional Manual does not exist! Operation failed!");
            }
        }
        catch (IOException ex)
        {
        }

    }//GEN-LAST:event_openHelpDocumentMenuItemActionPerformed

    private void automaticallyTagFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_automaticallyTagFilesMenuItemActionPerformed
    {//GEN-HEADEREND:event_automaticallyTagFilesMenuItemActionPerformed
        if (this.executingThread == null)
        {
            this.autoTagDialog.setVisible(true);
            int selectionSize = this.TagListAutoTag.getSelectedValuesList().size();

            newMessage("Preparing for automatic tagging...");

            if (this.autoTagStatus == false)
            {
                newMessage("Automatic tagging operation cancelled!");
            }

            else if (selectionSize > 1)
            {
                newMessage("Multiple tags selected! Please select one tag!");
            }

            else if (selectionSize == 0)
            {
                newMessage("No tags were selected. Please select one tag!");
            }

            else if (selectionSize == 1)
            {
                this.rm.setState(ResearchKnowledgeManager.activeState.ACTIVE);

                this.executingThread = new Thread(() ->
                {
                    TagClass selectedTagClass = ((TagClass) this.TagListAutoTag.getSelectedValue());

                    newMessage("Looking for documents fitting the following tag: " + selectedTagClass.toString());

                    Vector<String> tagKeyWords = selectedTagClass.keywords;

                    if (tagKeyWords != null && tagKeyWords.size() > 0)
                    {
                        rm.missingFiles.clear();

                        Vector<FileClass> reSaveFiles = new Vector<>(2);
                        Vector<FileClass> missingFiles = new Vector<>(2);
                        Vector<FileClass> otherBadFiles = new Vector<>(2);

                        this.progressBar.setValue(0);
                        this.progressBar.setMaximum(rm.Files.size());

                        for (FileClass f : rm.Files)
                        {
                            this.progressBar.setValue(this.progressBar.getValue() + 1);
                            this.progressBar.setString((int) ((float) 100 * this.progressBar.getValue() / this.progressBar.getMaximum()) + "%");

                            File buffer = new File(f.toString());

                            if (buffer.exists() && buffer.length() > minimumValidFileSize)
                            {
                                ProcessFileTest pft = new ProcessFileTest(this.rm);

                                if (pft.isProcessableFile(f))
                                {
                                    try
                                    {
                                        String fileContent = pft.getFileContent(f);

                                        if (fileContent != null && pft.TagSearch(tagKeyWords, fileContent, this.sensitivitySlider.getValue()))
                                        {
                                            newMessage(f.toString() + " is now associated with the tag: " + selectedTagClass.toString());
                                            f.addTag(selectedTagClass.toString());
                                        }
                                    }
                                    catch (org.apache.poi.poifs.filesystem.NotOLE2FileException e)
                                    {
                                        //System.err.println("Found a word file that needs to be resaved: " + fc.toString());
                                        reSaveFiles.add(f);
                                    }

                                    catch (FileNotFoundException e)
                                    {
                                        //System.err.println("Found a file that is missing: " + fc.toString());
                                        missingFiles.add(f);
                                    }

                                    catch (org.apache.poi.poifs.filesystem.OfficeXmlFileException e)
                                    {
                                        //System.err.println("Found a XML file that needs to be resaved:" + fc.toString());
                                        reSaveFiles.add(f);
                                    }

                                    catch (Exception e)
                                    {
                                        otherBadFiles.add(f);
                                        // System.err.println("A file with an undetermined exception was found! " + f.toString());
                                        // e.printStackTrace();
                                    }
                                }
                            }

                            else
                            {
                                rm.missingFiles.add(f);
                            }
                        }

                        if (!reSaveFiles.isEmpty())
                        {
                            ErrorHandleFunction(reSaveFiles, UserInterface.ErrorType.RESAVE);
                        }

                        if (!missingFiles.isEmpty())
                        {
                            ErrorHandleFunction(missingFiles, UserInterface.ErrorType.MISSING);
                        }

                        if (!otherBadFiles.isEmpty())
                        {
                            ErrorHandleFunction(otherBadFiles, UserInterface.ErrorType.OTHER);
                        }

                    }

                    else
                    {
                        newMessage("This tag has no keywords! Search could not be completed...");
                    }

                    this.TagListAutoTag.clearSelection();
                    this.rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                    this.executingThread = null;

                    newMessage(this.rm.lineSeparator);

                    this.progressBar.setString("Task completed!");

                });

                this.startTask();
            }

            else
            {

            }

        }

        else
        {
            newMessage("Could not prepare the system for the selected task! A task is already in execution!");
        }

    }//GEN-LAST:event_automaticallyTagFilesMenuItemActionPerformed

    private void autoTagSubmitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_autoTagSubmitActionPerformed
    {//GEN-HEADEREND:event_autoTagSubmitActionPerformed
        this.autoTagStatus = true;
        this.autoTagDialog.setVisible(false);
    }//GEN-LAST:event_autoTagSubmitActionPerformed

    private void tagKeywordListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_tagKeywordListValueChanged
    {//GEN-HEADEREND:event_tagKeywordListValueChanged
        this.tagKeywordList.clearSelection();
    }//GEN-LAST:event_tagKeywordListValueChanged

    private void TagListAutoTagValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_TagListAutoTagValueChanged
    {//GEN-HEADEREND:event_TagListAutoTagValueChanged
        DefaultListModel buffer = new DefaultListModel();

        if (this.TagListAutoTag.getSelectedValue() != null)
        {
            for (String s : ((TagClass) this.TagListAutoTag.getSelectedValue()).keywords)
            {
                buffer.addElement(s);
            }

            if (buffer.isEmpty())
            {
                buffer.addElement("The selected tag has no keywords");
                this.autoTagSubmit.setEnabled(false);
            }

            else
            {
                this.autoTagSubmit.setEnabled(true);
            }
        }

        else
        {
            buffer.addElement("No tag selected!");
            this.autoTagSubmit.setEnabled(false);
            this.TagListAutoTag.clearSelection();
        }

        this.tagKeywordList.setModel(buffer);
    }//GEN-LAST:event_TagListAutoTagValueChanged

    private void autoTagDialogWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_autoTagDialogWindowClosing
    {//GEN-HEADEREND:event_autoTagDialogWindowClosing
        this.autoTagStatus = false;
    }//GEN-LAST:event_autoTagDialogWindowClosing

    private void modifyKeywordsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_modifyKeywordsButtonActionPerformed
    {//GEN-HEADEREND:event_modifyKeywordsButtonActionPerformed
        TagClass buffer = (TagClass) this.TagTree.getSelectionPath().getLastPathComponent();
        String sbuffer = "";

        this.modifyKeywordsDialog.setTitle("Tag Keywords for \"" + buffer.toString() + "\" Tag");

        for (String s : buffer.keywords)
        {
            if (!s.isEmpty())
            {
                sbuffer += s + ",";
            }
        }

        this.modifyKeywordsTextArea.setText(sbuffer);
        this.modifyKeywordsDialog.setVisible(true);

        // Initialize keyword area
    }//GEN-LAST:event_modifyKeywordsButtonActionPerformed

    private void saveKeywordsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveKeywordsButtonActionPerformed
    {//GEN-HEADEREND:event_saveKeywordsButtonActionPerformed
        TagClass buffer = (TagClass) this.TagTree.getSelectionPath().getLastPathComponent();
        String[] stringArrayBuffer = this.modifyKeywordsTextArea.getText().split(",");
        buffer.keywords.clear();
        for (String s : stringArrayBuffer)
        {
            if (!s.isEmpty())
            {
                buffer.keywords.add(s.trim().toLowerCase());
            }
        }

        newMessage("Keyword(s) successfully saved for the following tag:" + buffer.toString());
        newMessage(rm.lineSeparator);

        this.modifyKeywordsDialog.dispose();
    }//GEN-LAST:event_saveKeywordsButtonActionPerformed

    private void progressBarStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_progressBarStateChanged
    {//GEN-HEADEREND:event_progressBarStateChanged
        if (rm.ui.progressBar.getMaximum() > 0)
        {
            rm.ui.progressBar.setString(100 * rm.ui.progressBar.getValue() / rm.ui.progressBar.getMaximum() + "%");
        }
        else
        {
            rm.ui.progressBar.setString("0%");
        }
    }//GEN-LAST:event_progressBarStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AddTagFilePopupMenuItem;
    protected javax.swing.JTree FileExplorerTree;
    private javax.swing.JScrollPane FileExplorerTreePane;
    private javax.swing.JTree FileTree;
    private javax.swing.JScrollPane FileTreePane;
    private javax.swing.JMenu IndexingMenu;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenuItem OpenFilePopupMenuItem;
    private javax.swing.JButton PerformSearchButton;
    private javax.swing.JPanel PrimaryActionPanel;
    private javax.swing.JMenuItem RemoveTagFilePopupMenuItem;
    private javax.swing.JMenuItem RootFolderPopupMenuItem;
    private javax.swing.JTextArea SearchKeyWordsTextArea;
    private javax.swing.JScrollPane SearchKeywordScrollPane;
    private javax.swing.JScrollPane SearchResultsListScrollPane;
    private javax.swing.JPopupMenu SearchResultsPopupMenu;
    private javax.swing.JMenu SystemMenu;
    protected javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JList TagItemSearchList;
    private javax.swing.JList TagListAutoTag;
    private javax.swing.JScrollPane TagListItemScrollPane;
    private javax.swing.JScrollPane TagListItemScrollPane1;
    private javax.swing.JTree TagTree;
    private javax.swing.JScrollPane TagTreePane;
    private javax.swing.JMenu TagsMenu;
    private javax.swing.JButton addFileToSystemButton;
    private javax.swing.JButton addFileToTagButton;
    private javax.swing.JButton addTagDialogButton;
    private javax.swing.JButton addTagToFileButton;
    private javax.swing.JDialog autoTagDialog;
    private javax.swing.JButton autoTagSubmit;
    private javax.swing.JMenuItem automaticallyTagFilesMenuItem;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton cancelDeleteTagButton;
    private javax.swing.JMenuItem changeRepositoryFolderMenuItem;
    private javax.swing.JMenuItem cleanDataFilesMenuItem;
    private javax.swing.JButton confirmDeleteTagButton;
    private javax.swing.JDialog confirmTagDeleteDialog;
    private javax.swing.JPanel customActionPane;
    private javax.swing.JButton deleteTagFromSystemButton;
    private javax.swing.JMenuItem indexAllFilesMenuItem;
    private javax.swing.JMenuItem indexNewFilesMenuItem;
    private javax.swing.JLabel instructionsTagDialogLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JDialog missingErrorHandlingFilesDialog;
    private javax.swing.JTextArea missingErrorMessageTextArea;
    private javax.swing.JList missingFileJList;
    private javax.swing.JScrollPane modifyKeywordScrollPane;
    private javax.swing.JButton modifyKeywordsButton;
    private javax.swing.JDialog modifyKeywordsDialog;
    private javax.swing.JTextArea modifyKeywordsTextArea;
    private javax.swing.JButton newTagButton;
    private javax.swing.JDialog newTagDialog;
    private javax.swing.JMenuItem openHelpDocumentMenuItem;
    private javax.swing.JDialog otherErrorHandlingFilesDialog;
    private javax.swing.JTextArea otherErrorMessageTextArea;
    private javax.swing.JList otherFileJList;
    private javax.swing.JButton pauseButton;
    protected javax.swing.JProgressBar progressBar;
    private javax.swing.JButton quitTagDialogButton;
    private javax.swing.JButton removeFileFromTagButton;
    private javax.swing.JButton removeTagFromFileButton;
    private javax.swing.JDialog resaveErrorHandlingFilesDialog;
    private javax.swing.JTextArea resaveErrorMessageTextArea;
    private javax.swing.JList resaveFileJList;
    private javax.swing.JButton resumeButton;
    private javax.swing.JMenuItem safelyExitSystem;
    private javax.swing.JButton saveKeywordsButton;
    private javax.swing.JList searchResultsList;
    private javax.swing.JSlider searchSlider;
    private javax.swing.JSlider sensitivitySlider;
    private javax.swing.JList statusList;
    private javax.swing.JScrollPane statusMessageScrollPane;
    private javax.swing.JScrollPane tagKeyWordScrollPane;
    private javax.swing.JList tagKeywordList;
    private javax.swing.JTextField userInputTagDialogTextField;
    protected javax.swing.JPanel welcomePanel;
    private javax.swing.JTextArea welcomeTextArea;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
