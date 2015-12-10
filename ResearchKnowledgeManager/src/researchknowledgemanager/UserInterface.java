/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;
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

    /**
     * Sets the various task buttons depending on the state of the system
     *
     * @see ResearchKnowledgeManager.activeState
     */
    void handleState()
    {
        switch (rm.actionStatus)
        {
            case READY:
            {
                startButton.setEnabled(true);
                cancelButton.setEnabled(true);
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(false);
                break;
            }

            case INACTIVE:
            {
                startButton.setEnabled(false);
                cancelButton.setEnabled(false);
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(false);
                break;
            }

            case ACTIVE:
            {
                startButton.setEnabled(false);
                cancelButton.setEnabled(true);
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(true);
                break;
            }

            case PAUSED:
            {
                startButton.setEnabled(false);
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
            for (int i = 0; i < rm.Tags.size(); i++)
            {
                if (!TagItemListModel.contains(rm.Tags.get(i)))
                {
                    this.TagItemListModel.addElement(this.rm.Tags.get(i));
                }
            }

        });

    }

    public void autoIndexAll()
    {
        this.indexAllFilesMenuItemActionPerformed(null);
        this.startButtonActionPerformed(null);
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
        TagFilePopupMenuItem = new javax.swing.JMenuItem();
        newTagDialog = new javax.swing.JDialog();
        addTagDialogButton = new javax.swing.JButton();
        quitTagDialogButton = new javax.swing.JButton();
        userInputTagDialogTextField = new javax.swing.JTextField();
        instructionsTagDialogLabel = new javax.swing.JLabel();
        deleteTagFromSystemButton = new javax.swing.JButton();
        confirmTagDeleteDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        confirmDeleteTagButton = new javax.swing.JButton();
        cancelDeleteTagButton = new javax.swing.JButton();
        TabbedPane = new javax.swing.JTabbedPane();
        welcomePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        welcomeTextArea = new javax.swing.JTextArea();
        jSeparator3 = new javax.swing.JSeparator();
        FileExplorerTreePane = new javax.swing.JScrollPane();
        FileExplorerTree = new javax.swing.JTree();
        TagTreePane = new javax.swing.JScrollPane();
        TagTree = new javax.swing.JTree();
        FileTreePane = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        statusMessageScrollPane = new javax.swing.JScrollPane();
        statusList = new javax.swing.JList();
        PrimaryActionPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        resumeButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        customActionPane = new javax.swing.JPanel();
        TagListItemScrollPane = new javax.swing.JScrollPane();
        TagItemSearchList = new javax.swing.JList();
        SearchKeywordScrollPane = new javax.swing.JScrollPane();
        SearchKeyWordsTextArea = new javax.swing.JTextArea();
        StartSearchButton = new javax.swing.JButton();
        SearchResultsListScrollPane = new javax.swing.JScrollPane();
        searchResultsList = new javax.swing.JList();
        progressBar = new javax.swing.JProgressBar();
        MenuBar = new javax.swing.JMenuBar();
        SystemMenu = new javax.swing.JMenu();
        changeRepositoryFolderMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        cleanDataFilesMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        safelyExitSystem = new javax.swing.JMenuItem();
        openHelpDocumentMenuItem = new javax.swing.JMenuItem();
        TagsMenu = new javax.swing.JMenu();
        automaticallyTagFilesMenuItem = new javax.swing.JMenuItem();
        SearchesMenu = new javax.swing.JMenu();
        initiateSearchQueryMenuItem = new javax.swing.JMenuItem();
        IndexingMenu = new javax.swing.JMenu();
        indexAllFilesMenuItem = new javax.swing.JMenuItem();
        indexNewFilesMenuItem = new javax.swing.JMenuItem();

        modifyKeywordsButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        modifyKeywordsButton.setText("Modify Tag Keywords");
        modifyKeywordsButton.setActionCommand("");
        modifyKeywordsButton.setAlignmentX(0.5F);
        modifyKeywordsButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

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

        TagFilePopupMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        TagFilePopupMenuItem.setText("Tag File(s)");
        TagFilePopupMenuItem.setToolTipText("Tags the selected files with the tags selected under \"" + this.tagSearchListName + "\" window");
        TagFilePopupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                TagFilePopupMenuItemActionPerformed(evt);
            }
        });
        SearchResultsPopupMenu.add(TagFilePopupMenuItem);

        newTagDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        newTagDialog.setTitle("Add New Tag to Research Knowledge Manager");
        newTagDialog.setAlwaysOnTop(true);
        newTagDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        newTagDialog.setMaximumSize(this.newTagDialog.getPreferredSize());
        newTagDialog.setMinimumSize(this.newTagDialog.getPreferredSize());
        newTagDialog.setModal(true);
        newTagDialog.setPreferredSize(new java.awt.Dimension(410, 151));
        newTagDialog.setResizable(false);
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
                    .addComponent(instructionsTagDialogLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        newTagDialogLayout.setVerticalGroup(
            newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newTagDialogLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(instructionsTagDialogLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userInputTagDialogTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newTagDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addTagDialogButton)
                    .addComponent(quitTagDialogButton))
                .addContainerGap())
        );

        this.newTagDialog.setSize(this.newTagDialog.getPreferredSize());

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

        confirmTagDeleteDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        confirmTagDeleteDialog.setTitle("Tag Deletion Confirmation");
        confirmTagDeleteDialog.setFont(this.getFont());
        confirmTagDeleteDialog.setMaximumSize(new java.awt.Dimension(466, 162));
        confirmTagDeleteDialog.setMinimumSize(new java.awt.Dimension(466, 162));
        confirmTagDeleteDialog.setModal(true);
        confirmTagDeleteDialog.setPreferredSize(new java.awt.Dimension(466, 162));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Research Knowledge Manager - SE Senior Design UTD Fall 2015");
        setBackground(new java.awt.Color(153, 153, 153));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setEnabled(false);
        setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        setMaximumSize(new java.awt.Dimension(1280, 700));
        setMinimumSize(new java.awt.Dimension(1280, 700));
        setPreferredSize(new java.awt.Dimension(1280, 700));
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
        welcomePanel.add(jSeparator3);

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
        statusList.setFont(new java.awt.Font("Verdana", 2, 10)); // NOI18N
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
        PrimaryActionPanel.setLayout(new java.awt.GridLayout(1, 0, 2, 0));

        startButton.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        startButton.setText("Start Task");
        startButton.setActionCommand("");
        startButton.setAlignmentX(0.5F);
        startButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        startButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        startButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startButtonActionPerformed(evt);
            }
        });
        PrimaryActionPanel.add(startButton);
        startButton.getAccessibleContext().setAccessibleDescription("");

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
        PrimaryActionPanel.add(resumeButton);
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
        PrimaryActionPanel.add(pauseButton);
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
        PrimaryActionPanel.add(cancelButton);
        cancelButton.getAccessibleContext().setAccessibleDescription("");

        mainPanel.setBackground(new java.awt.Color(229, 229, 229));
        mainPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED)));

        customActionPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        customActionPane.setMinimumSize(new java.awt.Dimension(817, 28));
        customActionPane.setLayout(new java.awt.GridLayout(0, 2, 2, 2));

        TagListItemScrollPane.setBorder(SearchKeywordScrollPane.getBorder());
        TagListItemScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), this.tagSearchListName, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        TagItemSearchList.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        TagItemSearchList.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        TagItemSearchList.setModel(this.TagItemListModel);
        TagItemSearchList.setToolTipText("The list of tags currently in the system. You can select or deselect multiple tags by holding \"Shift\" or \"Control\"");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), TagItemSearchList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        TagListItemScrollPane.setViewportView(TagItemSearchList);

        SearchKeywordScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        SearchKeywordScrollPane.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Keyword Query Terms", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4))); // NOI18N

        SearchKeyWordsTextArea.setEditable(false);
        SearchKeyWordsTextArea.setBackground(new java.awt.Color(204, 204, 204));
        SearchKeyWordsTextArea.setColumns(25);
        SearchKeyWordsTextArea.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        SearchKeyWordsTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.white, null));
        SearchKeyWordsTextArea.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), SearchKeyWordsTextArea, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        SearchKeywordScrollPane.setViewportView(SearchKeyWordsTextArea);

        StartSearchButton.setText("Perform Search");
        StartSearchButton.setToolTipText("Searches for files with the specified tags and/or keywords");
        StartSearchButton.setActionCommand("");
        StartSearchButton.setAlignmentX(0.5F);
        StartSearchButton.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), StartSearchButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        StartSearchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                StartSearchButtonActionPerformed(evt);
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
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StartSearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SearchKeywordScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(SearchResultsListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SearchResultsListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(SearchKeywordScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StartSearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(TagListItemScrollPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customActionPane, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
        SystemMenu.add(jSeparator1);
        SystemMenu.add(jSeparator2);

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

        SystemMenu.add(jSeparator5);

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

        MenuBar.add(SystemMenu);

        TagsMenu.setText("Tags");
        TagsMenu.setToolTipText("Contains options related to file tagging");
        TagsMenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        automaticallyTagFilesMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        automaticallyTagFilesMenuItem.setText("Automatically Tag Files");
        automaticallyTagFilesMenuItem.setToolTipText("This featured is not implemented currently...");
        automaticallyTagFilesMenuItem.setEnabled(false);
        TagsMenu.add(automaticallyTagFilesMenuItem);

        MenuBar.add(TagsMenu);

        SearchesMenu.setText("Searches");
        SearchesMenu.setToolTipText("Contains options related to performing searches");
        SearchesMenu.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        initiateSearchQueryMenuItem.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        initiateSearchQueryMenuItem.setText("Initiate Search Query");
        initiateSearchQueryMenuItem.setToolTipText("This featured is not implemented currently...");
        initiateSearchQueryMenuItem.setActionCommand("initiateSearchQuery");
        initiateSearchQueryMenuItem.setEnabled(false);
        SearchesMenu.add(initiateSearchQueryMenuItem);

        MenuBar.add(SearchesMenu);

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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusMessageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PrimaryActionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(PrimaryActionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Research Knowledge Manager - SE Senior Design");

        bindingGroup.bind();

        setSize(new java.awt.Dimension(1283, 700));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void SystemMenuMenuCanceled(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_SystemMenuMenuCanceled
        // TODO add your handling code here:
    }//GEN-LAST:event_SystemMenuMenuCanceled

    private void changeRepositoryFolderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRepositoryFolderMenuItemActionPerformed
        try
        {
            rm.askForRepository();
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
            this.initiateSearchQueryMenuItem.setEnabled(false);

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

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        if (executingThread != null && !executingThread.isAlive())
        {
            executingThread.start();
        }
        else
        {
            System.err.println("NULL EXCEPTION ERROR OCCURRED");
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void progressBarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_progressBarStateChanged
        if (rm.ui.progressBar.getMaximum() > 0)
        {
            rm.ui.progressBar.setString(100 * rm.ui.progressBar.getValue() / rm.ui.progressBar.getMaximum() + "%");
        }
        else
        {
            rm.ui.progressBar.setString("0%");
        }

    }//GEN-LAST:event_progressBarStateChanged

    private void indexAllFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexAllFilesMenuItemActionPerformed
        rm.setState(ResearchKnowledgeManager.activeState.READY);
        newMessage("Ready to index all files. Click \"Start Action\" to begin!");

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

                newMessage(rm.lineSeparator);
                rm.fileIndexer.saveIndexAll(rm.dataDirectory);

                // Thread cleanup
                this.rm.sortTagClass(this.rm.Tags);
                this.rm.sortFileClass(this.rm.Files);
                this.updateFileTree();
                this.updateTagTree();
                newMessage("Finished processing all files!");
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                rm.updateLastModified();
                executingThread = null;
            });
        }

        else
        {
            newMessage("Could not prepare the system for the selected task! A task is already in execution!");
        }

    }//GEN-LAST:event_indexAllFilesMenuItemActionPerformed

    private void indexNewFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexNewFilesMenuItemActionPerformed

        rm.setState(ResearchKnowledgeManager.activeState.READY);
        newMessage("Ready to index all files. Click \"Start Action\" to begin");

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
                this.rm.sortTagClass(this.rm.Tags);
                this.rm.sortFileClass(this.rm.Files);
                this.updateFileTree();
                this.updateTagTree();

                newMessage("Finished processing new files!");
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                rm.updateLastModified();
                executingThread = null;
            });
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
            case READY:
            {
                // May need to find better alternative to stop() method since the method is deprecated...
                executingThread.stop();
                executingThread = null;
                rm.setState(ResearchKnowledgeManager.activeState.INACTIVE);
                break;
            }
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
                for (int i = 0; i < selectedValues.size(); i++)
                {
                    fileItem.addTag(selectedValues.get(i).toString());
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

    private void StartSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartSearchButtonActionPerformed
        if (!this.SearchKeyWordsTextArea.getText().trim().equals(""))
        {
            newMessage("Searching with keywords is currently not implemented...");
            this.SearchKeyWordsTextArea.setText(null);
        }

        List<TagClass> selectedTags = this.TagItemSearchList.getSelectedValuesList();

        if (selectedTags.isEmpty())
        {
            newMessage("Search cannot be completed! No tag terms were selected.");
            newMessage(rm.lineSeparator);
        }
        else
        {
            newMessage("Performing Tag-based Search...");

            Vector<FileClass> tagResults = rm.findTagsComplete(selectedTags);

            if (this.SearchKeyWordsTextArea.getText().trim().equals(""))
            {
                // rm.parseKeyWords(tagResults);
                // searchKeyWords();
            }

            DefaultListModel buffer = new DefaultListModel();

            for (int i = 0; i < tagResults.size(); i++)
            {
                buffer.addElement(tagResults.get(i));
            }
            updateSearchResultsName(buffer.size());

            this.searchResultsList.setModel(buffer);

            newMessage("Tag search completed! Results are displayed in \"Search Results\"");
            newMessage(rm.lineSeparator);
        }

    }//GEN-LAST:event_StartSearchButtonActionPerformed

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
                for (int i = 0; i < selectedItems.size(); i++)
                {
                    newMessage("Launching the following file: " + selectedItems.get(i).toString());
                    Desktop.getDesktop().open(new File(selectedItems.get(i).toString()));
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
                for (int i = 0; i < selectedItems.size(); i++)
                {
                    File buffer = new File(selectedItems.get(i).toString());

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
                        // this.modifyKeywordsButton.setEnabled(true);
                        this.modifyKeywordsButton.setEnabled(false);
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

    private void TagFilePopupMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TagFilePopupMenuItemActionPerformed
    {//GEN-HEADEREND:event_TagFilePopupMenuItemActionPerformed
        List<TagClass> selectedTagValues = this.TagItemSearchList.getSelectedValuesList();
        List<FileClass> selectedFileValues = this.searchResultsList.getSelectedValuesList();
        newMessage("Beginning tagging process...");

        if (!selectedTagValues.isEmpty() && !selectedFileValues.isEmpty())
        {
            // Adds selected tags to the selected files
            for (int i = 0; i < selectedTagValues.size(); i++)
            {
                // Adds a tag for each selected file
                for (int j = 0; j < selectedFileValues.size(); j++)
                {
                    selectedFileValues.get(j).addTag(selectedTagValues.get(i).toString());
                    selectedFileValues.get(j).associatedTags.sort(null);
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
    }//GEN-LAST:event_TagFilePopupMenuItemActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTree FileExplorerTree;
    private javax.swing.JScrollPane FileExplorerTreePane;
    private javax.swing.JTree FileTree;
    private javax.swing.JScrollPane FileTreePane;
    private javax.swing.JMenu IndexingMenu;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenuItem OpenFilePopupMenuItem;
    private javax.swing.JPanel PrimaryActionPanel;
    private javax.swing.JMenuItem RootFolderPopupMenuItem;
    private javax.swing.JTextArea SearchKeyWordsTextArea;
    private javax.swing.JScrollPane SearchKeywordScrollPane;
    private javax.swing.JScrollPane SearchResultsListScrollPane;
    private javax.swing.JPopupMenu SearchResultsPopupMenu;
    private javax.swing.JMenu SearchesMenu;
    private javax.swing.JButton StartSearchButton;
    private javax.swing.JMenu SystemMenu;
    protected javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JMenuItem TagFilePopupMenuItem;
    private javax.swing.JList TagItemSearchList;
    private javax.swing.JScrollPane TagListItemScrollPane;
    private javax.swing.JTree TagTree;
    private javax.swing.JScrollPane TagTreePane;
    private javax.swing.JMenu TagsMenu;
    private javax.swing.JButton addFileToSystemButton;
    private javax.swing.JButton addFileToTagButton;
    private javax.swing.JButton addTagDialogButton;
    private javax.swing.JButton addTagToFileButton;
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
    private javax.swing.JMenuItem initiateSearchQueryMenuItem;
    private javax.swing.JLabel instructionsTagDialogLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton modifyKeywordsButton;
    private javax.swing.JButton newTagButton;
    private javax.swing.JDialog newTagDialog;
    private javax.swing.JMenuItem openHelpDocumentMenuItem;
    private javax.swing.JButton pauseButton;
    protected javax.swing.JProgressBar progressBar;
    private javax.swing.JButton quitTagDialogButton;
    private javax.swing.JButton removeFileFromTagButton;
    private javax.swing.JButton removeTagFromFileButton;
    private javax.swing.JButton resumeButton;
    private javax.swing.JMenuItem safelyExitSystem;
    private javax.swing.JList searchResultsList;
    private javax.swing.JButton startButton;
    private javax.swing.JList statusList;
    private javax.swing.JScrollPane statusMessageScrollPane;
    private javax.swing.JTextField userInputTagDialogTextField;
    protected javax.swing.JPanel welcomePanel;
    private javax.swing.JTextArea welcomeTextArea;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
