import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class GestionApp {
    // Componentes principales
    private JFrame loginFrame;
    private JFrame mainFrame;

    // Componentes de conexión
    private JTextField ipField;
    private JTextField portField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField dbField;
    private Connection connection;

    // Constructor principal
    public GestionApp() {
        createLoginScreen();
    }

    // Crear pantalla de login/conexión
    private void createLoginScreen() {
        loginFrame = new JFrame("Hola!, ¿Donde debo conectarme?");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Etiquetas y campos
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Dirección IP:"), gbc);

        gbc.gridx = 1;
        ipField = new JTextField("localhost", 15);
        panel.add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Puerto:"), gbc);

        gbc.gridx = 1;
        portField = new JTextField("3306", 15);
        panel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        userField = new JTextField("root", 15);
        panel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Base de Datos:"), gbc);

        gbc.gridx = 1;
        dbField = new JTextField(15);
        panel.add(dbField, gbc);

        // Botón de conexión
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton connectButton = new JButton("Conectar");
        panel.add(connectButton, gbc);

        // Acción del botón
        connectButton.addActionListener(e -> connectToDatabase());

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    // Métodos para conectar a la base de datos
    private void connectToDatabase() {
        String ip = ipField.getText();
        String port = portField.getText();
        String user = userField.getText();
        String password = new String(passwordField.getPassword());
        String db = dbField.getText();

        try {
            // Cargar el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Crear la URL de conexión
            String url = "jdbc:mysql://" + ip + ":" + port + "/" + db;

            // Intentar conectars
            connection = DriverManager.getConnection(url, user, password);

            JOptionPane.showMessageDialog(loginFrame, "Conexión exitosa a la base de datos",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Cerrar ventana de login y abrir la principal
            loginFrame.dispose();
            createMainInterface();

        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(loginFrame, "Error!!! No puedo encontrar el controlador",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(loginFrame, "Error de conexion :(" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Crear la interfaz principal
    private void createMainInterface() {
        mainFrame = new JFrame("Sistema de Gestion Kolibri (Alpha)");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        // Crear el panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Añadir las pestañas para cada entidad
        tabbedPane.addTab("Empleados", createEntityPanel("empleados"));
        tabbedPane.addTab("Entregas", createEntityPanel("entregas"));
        tabbedPane.addTab("Ventas", createEntityPanel("ventas"));
        tabbedPane.addTab("Clientes", createEntityPanel("clientes"));
        tabbedPane.addTab("Inventario", createEntityPanel("inventario"));
        tabbedPane.addTab("Proveedores", createEntityPanel("proveedores"));

        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);

        // Cargar datos iniciales
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
            String entityName = tabbedPane.getTitleAt(i).toLowerCase();
            loadEntityData(panel, entityName);
        }

        // Añadir listener para cerrar la conexión al cerrar la aplicación
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        });
    }

    // Crear panel para cada entidad
    private JPanel createEntityPanel(String entityName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior con botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Añadir");
        JButton deleteButton = new JButton("Eliminar");
        JButton refreshButton = new JButton("Actualizar");

        actionPanel.add(addButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);

        // Área de texto para mostrar información
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);

        // Tabla para mostrar datos
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        // Añadir componentes al panel
        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Configurar acciones de los botones
        addButton.addActionListener(e -> showAddDialog(entityName, panel));
        deleteButton.addActionListener(e -> deleteSelectedItem(dataTable, entityName, panel));
        refreshButton.addActionListener(e -> loadEntityData(panel, entityName));

        // Listener para mostrar detalles al seleccionar una fila
        dataTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && dataTable.getSelectedRow() != -1) {
                int row = dataTable.getSelectedRow();
                StringBuilder details = new StringBuilder();
                for (int i = 0; i < dataTable.getColumnCount(); i++) {
                    details.append(dataTable.getColumnName(i))
                            .append(": ")
                            .append(dataTable.getValueAt(row, i))
                            .append("\n");
                }
                infoArea.setText(details.toString());
            }
        });

        return panel;
    }

    // Cargar datos de la entidad
    private void loadEntityData(JPanel panel, String entityName) {
        try {
            // Obtener la tabla
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // Limpiar modelo
            model.setRowCount(0);
            model.setColumnCount(0);

            // Preparar consulta según la entidad
            String query = "SELECT * FROM " + entityName;

            // Caso especial para inventario
            if (entityName.equals("inventario")) {
                query = "SELECT i.*, m.nombre as modelo, c.nombre as componente " +
                        "FROM inventario i " +
                        "LEFT JOIN modelos_computadoras m ON i.modelo_id = m.id " +
                        "LEFT JOIN componentes_computadoras c ON i.componente_id = c.id";
            }

            // Caso especial para ventas
            if (entityName.equals("ventas")) {
                query = "SELECT v.*, c.nombre as cliente, e.nombre as empleado " +
                        "FROM ventas v " +
                        "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                        "LEFT JOIN empleados e ON v.empleado_id = e.id";
            }

            // Caso especial para entregas
            if (entityName.equals("entregas")) {
                query = "SELECT e.*, v.id as venta_num, emp.nombre as empleado " +
                        "FROM entregas e " +
                        "LEFT JOIN ventas v ON e.venta_id = v.id " +
                        "LEFT JOIN empleados emp ON e.empleado_id = emp.id";
            }

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Obtener metadatos para las columnas
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Añadir columnas al modelo
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Añadir filas al modelo
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                model.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error al cargar datos de " + entityName + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mostrar diálogo para añadir un nuevo elemento
    private void showAddDialog(String entityName, JPanel parentPanel) {
        try {
            // Obtener estructura de la tabla
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, entityName, null);

            // Crear diálogo
            JDialog dialog = new JDialog(mainFrame, "Añadir " + entityName, true);
            dialog.setSize(500, 500);
            dialog.setLocationRelativeTo(mainFrame);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Lista para almacenar los campos
            java.util.List<JComponent> fields = new java.util.ArrayList<>();
            java.util.List<String> columnNames = new java.util.ArrayList<>();

            int row = 0;

            // Crear campos para cada columna
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");

                // Ignorar columnas auto-incrementales (normalmente IDs)
                if (columnName.equalsIgnoreCase("id")) {
                    columnNames.add(columnName);
                    fields.add(new JTextField("AUTO"));
                    continue;
                }

                gbc.gridx = 0;
                gbc.gridy = row;
                panel.add(new JLabel(columnName + ":"), gbc);

                gbc.gridx = 1;

                // Crear el campo apropiado según el tipo de dato y la entidad
                JComponent field;

                // Caso especial para ventas: cliente_id y empleado_id como ComboBox
                if (entityName.equals("ventas")) {
                    if (columnName.equals("cliente_id")) {
                        JComboBox<ComboItem> comboBox = new JComboBox<>();
                        loadClientsIntoComboBox(comboBox);
                        field = comboBox;
                    } else if (columnName.equals("empleado_id")) {
                        JComboBox<ComboItem> comboBox = new JComboBox<>();
                        loadEmployeesIntoComboBox(comboBox);
                        field = comboBox;
                    } else if (dataType.contains("INT")) {
                        field = new JTextField(10);
                    } else if (dataType.contains("VARCHAR") || dataType.contains("TEXT")) {
                        field = new JTextField(15);
                    } else if (dataType.contains("DATE") || dataType.contains("TIME")) {
                        field = new JTextField("YYYY-MM-DD", 10);
                    } else if (dataType.contains("DECIMAL") || dataType.contains("FLOAT")) {
                        field = new JTextField("0.00", 10);
                    } else {
                        field = new JTextField(15);
                    }
                }
                // Caso especial para entregas: venta_id y empleado_id como ComboBox
                else if (entityName.equals("entregas")) {
                    if (columnName.equals("venta_id")) {
                        JComboBox<ComboItem> comboBox = new JComboBox<>();
                        loadSalesIntoComboBox(comboBox);
                        field = comboBox;
                    } else if (columnName.equals("empleado_id")) {
                        JComboBox<ComboItem> comboBox = new JComboBox<>();
                        loadEmployeesIntoComboBox(comboBox);
                        field = comboBox;
                    } else if (dataType.contains("INT")) {
                        field = new JTextField(10);
                    } else if (dataType.contains("VARCHAR") || dataType.contains("TEXT")) {
                        field = new JTextField(15);
                    } else if (dataType.contains("DATE") || dataType.contains("TIME")) {
                        field = new JTextField("YYYY-MM-DD", 10);
                    } else if (dataType.contains("DECIMAL") || dataType.contains("FLOAT")) {
                        field = new JTextField("0.00", 10);
                    } else {
                        field = new JTextField(15);
                    }
                }
                // Caso general para otras entidades
                else {
                    if (dataType.contains("INT")) {
                        field = new JTextField(10);
                    } else if (dataType.contains("VARCHAR") || dataType.contains("TEXT")) {
                        field = new JTextField(15);
                    } else if (dataType.contains("DATE") || dataType.contains("TIME")) {
                        field = new JTextField("YYYY-MM-DD", 10);
                    } else if (dataType.contains("DECIMAL") || dataType.contains("FLOAT")) {
                        field = new JTextField("0.00", 10);
                    } else {
                        field = new JTextField(15);
                    }
                }

                panel.add(field, gbc);
                fields.add(field);
                columnNames.add(columnName);
                row++;
            }

            columns.close();

            // Botones de acción
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Guardar");
            JButton cancelButton = new JButton("Cancelar");

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            // Acción de guardar
            saveButton.addActionListener(e -> {
                try {
                    // Construir la consulta INSERT
                    StringBuilder query = new StringBuilder("INSERT INTO " + entityName + " (");
                    StringBuilder values = new StringBuilder(" VALUES (");

                    for (int i = 0; i < columnNames.size(); i++) {
                        // Ignorar campos AUTO (IDs)
                        if (fields.get(i) instanceof JTextField &&
                                ((JTextField)fields.get(i)).getText().equals("AUTO")) {
                            continue;
                        }

                        if (i > 0 && !values.toString().endsWith("(")) {
                            query.append(", ");
                            values.append(", ");
                        }

                        query.append(columnNames.get(i));

                        // Obtener el valor según el tipo de componente
                        if (fields.get(i) instanceof JComboBox) {
                            ComboItem selectedItem = (ComboItem)((JComboBox<?>)fields.get(i)).getSelectedItem();
                            assert selectedItem != null;
                            values.append(selectedItem.value());
                        } else if (fields.get(i) instanceof JTextField) {
                            String text = ((JTextField)fields.get(i)).getText();
                            // Comprobar si es numérico
                            try {
                                Double.parseDouble(text);
                                values.append(text);
                            } catch (NumberFormatException ex) {
                                values.append("'").append(text).append("'");
                            }
                        }
                    }

                    query.append(")");
                    values.append(")");

                    // Ejecutar la consulta
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(query + values.toString());
                    stmt.close();

                    JOptionPane.showMessageDialog(dialog, "Registro añadido correctamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Recargar datos
                    loadEntityData(parentPanel, entityName);
                    dialog.dispose();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error al guardar: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Acción de cancelar
            cancelButton.addActionListener(e -> dialog.dispose());

            // Añadir componentes al diálogo
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error al preparar formulario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar clientes en un combobox
    private void loadClientsIntoComboBox(JComboBox<ComboItem> comboBox) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, CONCAT(nombre, ' ', apellido) as nombre_completo FROM clientes");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre_completo");
                comboBox.addItem(new ComboItem(nombre, id));
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            System.err.println("Error al cargar clientes: " + ex.getMessage());
        }
    }

    // Cargar empleados en un combobox
    private void loadEmployeesIntoComboBox(JComboBox<ComboItem> comboBox) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, CONCAT(nombre, ' ', apellido) as nombre_completo FROM empleados");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre_completo");
                comboBox.addItem(new ComboItem(nombre, id));
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            System.err.println("Error al cargar empleados: " + ex.getMessage());
        }
    }

    // Cargar ventas en un combobox
    private void loadSalesIntoComboBox(JComboBox<ComboItem> comboBox) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT v.id, CONCAT('Venta #', v.id, ' - Cliente: ', c.nombre, ' - Fecha: ', v.fecha) as descripcion " +
                            "FROM ventas v " +
                            "LEFT JOIN clientes c ON v.cliente_id = c.id"
            );

            while (rs.next()) {
                int id = rs.getInt("id");
                String descripcion = rs.getString("descripcion");
                comboBox.addItem(new ComboItem(descripcion, id));
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            System.err.println("Error al cargar ventas: " + ex.getMessage());
        }
    }

    // Eliminar elemento seleccionado
    private void deleteSelectedItem(JTable table, String entityName, JPanel panel) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Por favor, seleccione un elemento para eliminar",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar eliminación
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "¿Está seguro de que desea eliminar este elemento?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Obtener el ID del elemento seleccionado (asumiendo que está en la primera columna)
                Object idObj = table.getValueAt(selectedRow, 0);
                int id = Integer.parseInt(idObj.toString());

                // Ejecutar la consulta DELETE
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("DELETE FROM " + entityName + " WHERE id = " + id);
                stmt.close();

                JOptionPane.showMessageDialog(mainFrame,
                        "Elemento eliminado correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Recargar datos
                loadEntityData(panel, entityName);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error: ID no válido",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Clase auxiliar para elementos de combobox
    private record ComboItem(String label, int value) {
        @Override
        public String toString() {
            return label;
        }
    }

    // Métodos principal
    public static void main(String[] args) {
        // Establecer look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar la aplicación
        SwingUtilities.invokeLater(GestionApp::new);
    }
}