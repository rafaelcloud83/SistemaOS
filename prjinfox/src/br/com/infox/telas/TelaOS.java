package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Rafael
 */
public class TelaOS extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    private String tipo;

    public TelaOS() {
        initComponents();
        conexao = ModuloConexao.conector();
        controleBotoes(true);
        limparCamposEspecial();
    }

    private void pesquisarCliente() {
        String sql = "SELECT idcli as ID, nomecli as NOME, fonecli as TELEFONE FROM tbclientes WHERE nomecli LIKE ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtOsCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tblOsClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisarNomeCliente(String idCliente) {
        String sql = "SELECT nomecli FROM tbclientes WHERE idcli=" + idCliente;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtOsCliNome.setText(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisarOs() {
        String numOs = JOptionPane.showInputDialog("Número da OS?");
        String sql = "SELECT * FROM tbos WHERE os=" + numOs;
        //String sql = "SELECT O.os, dataos, tipo, situacao, equipamento, defeito, servico, tecnico, valor, C.nomecli FROM tbos as O inner join tbclientes as C on (O.idcli = C.idcli) WHERE os="+numOs;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtOsId.setText(rs.getString(1));
                txtOsData.setText(rs.getString(2));
                String rbtTipo = rs.getString(3);
                if (rbtTipo.equals("OS")) {
                    rbtOsOs.setSelected(true);
                    tipo = "OS";
                } else {
                    rbtOsOrc.setSelected(true);
                    tipo = "Orçamento";
                }
                cboOsSituacao.setSelectedItem(rs.getString(4));
                txtOsEquipamento.setText(rs.getString(5));
                txtOsDefeito.setText(rs.getString(6));
                txtOsServico.setText(rs.getString(7));
                txtOsTecnico.setText(rs.getString(8));
                txtOsValor.setText(rs.getString(9).replace(".", ","));
                txtOsCliId.setText(rs.getString(10));
                pesquisarNomeCliente(txtOsCliId.getText());
                controleBotoes(false);
                txtOsCliPesquisar.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Ordem de serviço não cadastrado");
                limparCamposEspecial();
                controleBotoes(true);
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "Ordem de serviço inválida");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            //System.out.println(e);
        }
    }

    private void adicionar() {
        String sql = "INSERT INTO tbos (tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli) VALUES (?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsSituacao.getSelectedItem().toString());
            pst.setString(3, txtOsEquipamento.getText());
            pst.setString(4, txtOsDefeito.getText());
            pst.setString(5, txtOsServico.getText());
            pst.setString(6, txtOsTecnico.getText());
            pst.setString(7, txtOsValor.getText().replace(",", "."));
            pst.setString(8, txtOsCliId.getText());
            if (txtOsEquipamento.getText().isEmpty() || txtOsDefeito.getText().isEmpty() || txtOsServico.getText().isEmpty() || txtOsCliId.getText().isEmpty() || txtOsValor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado == 1) {
                    JOptionPane.showMessageDialog(null, "Ordem de serviço gerada com sucesso");
                    limparCamposEspecial();
                    controleBotoes(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void alterar() {
        String sql = "UPDATE tbos SET tipo=?, situacao=?, equipamento=?, defeito=?, servico=?, tecnico=?, valor=? WHERE os=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsSituacao.getSelectedItem().toString());
            pst.setString(3, txtOsEquipamento.getText());
            pst.setString(4, txtOsDefeito.getText());
            pst.setString(5, txtOsServico.getText());
            pst.setString(6, txtOsTecnico.getText());
            pst.setString(7, txtOsValor.getText().replace(",", "."));
            pst.setString(8, txtOsId.getText());
            if (txtOsEquipamento.getText().isEmpty() || txtOsDefeito.getText().isEmpty() || txtOsServico.getText().isEmpty() || txtOsCliId.getText().isEmpty() || txtOsValor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
                int alterado = pst.executeUpdate();
                if (alterado == 1) {
                    JOptionPane.showMessageDialog(null, "Ordem de serviço alterada com sucesso");
                    limparCamposEspecial();
                    controleBotoes(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void excluir() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja excluir a OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tbos WHERE os=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtOsId.getText());
                int apagado = pst.executeUpdate();
                if (apagado == 1) {
                    JOptionPane.showMessageDialog(null, "Ordem de serviço excluída com sucesso");
                    limparCamposEspecial();
                    controleBotoes(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void imprimir(){
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão da OS ou do ORÇAMENTO?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                HashMap filtro = new HashMap();
                filtro.put("pos", Integer.parseInt(txtOsId.getText()));
                JasperPrint print = JasperFillManager.fillReport("C:\\Relatorio\\Java\\Infox\\os.jasper", filtro, conexao);
                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void limparCampos() {
        txtOsCliId.setText(null);
        txtOsCliNome.setText(null);
        txtOsCliPesquisar.setText(null);
        txtOsData.setText(null);
        txtOsDefeito.setText(null);
        txtOsEquipamento.setText(null);
        txtOsId.setText(null);
        txtOsServico.setText(null);
        txtOsTecnico.setText(null);
        txtOsValor.setText("0,00");
        ((DefaultTableModel) tblOsClientes.getModel()).setRowCount(0);
    }

    private void limparCamposEspecial() {
        limparCampos();
        cboOsSituacao.setSelectedIndex(0);
        rbtOsOrc.setSelected(true);
        txtOsCliPesquisar.setVisible(true);
    }

    public void setarCamposCliente() {
        int setar = tblOsClientes.getSelectedRow();
        txtOsCliId.setText(tblOsClientes.getModel().getValueAt(setar, 0).toString());
        txtOsCliNome.setText(tblOsClientes.getModel().getValueAt(setar, 1).toString());
    }

    private void controleBotoes(Boolean botao) {
        btnOsEdit.setEnabled(!botao);
        btnOsPrinter.setEnabled(!botao);
        btnOsDelete.setEnabled(!botao);
        btnOsRead.setEnabled(true);
        btnOsCreate.setEnabled(botao);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgOS = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtOsEquipamento = new javax.swing.JTextField();
        txtOsTecnico = new javax.swing.JTextField();
        txtOsValor = new javax.swing.JTextField();
        btnOsRead = new javax.swing.JButton();
        btnOsEdit = new javax.swing.JButton();
        btnOsPrinter = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtOsId = new javax.swing.JTextField();
        txtOsData = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rbtOsOrc = new javax.swing.JRadioButton();
        rbtOsOs = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        txtOsCliPesquisar = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOsClientes = new javax.swing.JTable();
        txtOsCliId = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtOsCliNome = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cboOsSituacao = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtOsDefeito = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtOsServico = new javax.swing.JTextField();
        btnOsCreate = new javax.swing.JButton();
        btnOsDelete = new javax.swing.JButton();

        setClosable(true);
        setTitle("Ordem de Serviço");
        setPreferredSize(new java.awt.Dimension(805, 604));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("*Situação");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Técnico");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Valor Total");

        txtOsEquipamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtOsTecnico.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtOsValor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnOsRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/read.png"))); // NOI18N
        btnOsRead.setToolTipText("Pesquisar");
        btnOsRead.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsRead.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsReadActionPerformed(evt);
            }
        });

        btnOsEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/update.png"))); // NOI18N
        btnOsEdit.setToolTipText("Editar");
        btnOsEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsEdit.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsEditActionPerformed(evt);
            }
        });

        btnOsPrinter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/print.png"))); // NOI18N
        btnOsPrinter.setToolTipText("Imprimir");
        btnOsPrinter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsPrinter.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsPrinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsPrinterActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("N° OS");

        txtOsId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOsId.setEnabled(false);

        txtOsData.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOsData.setEnabled(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Data");

        bgOS.add(rbtOsOrc);
        rbtOsOrc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rbtOsOrc.setText("Orçamento");
        rbtOsOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOsOrcActionPerformed(evt);
            }
        });

        bgOS.add(rbtOsOs);
        rbtOsOs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rbtOsOs.setText("OS");
        rbtOsOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOsOsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOsId))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbtOsOrc)
                                .addGap(58, 58, 58)
                                .addComponent(rbtOsOs))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtOsData, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtOsId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOsData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOsOrc)
                    .addComponent(rbtOsOs))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        txtOsCliPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOsCliPesquisar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtOsCliPesquisarMouseClicked(evt);
            }
        });
        txtOsCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOsCliPesquisarKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/pesquisar.png"))); // NOI18N

        tblOsClientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblOsClientes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblOsClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "NOME", "TELEFONE"
            }
        ));
        tblOsClientes.setFocusable(false);
        tblOsClientes.getTableHeader().setResizingAllowed(false);
        tblOsClientes.getTableHeader().setReorderingAllowed(false);
        tblOsClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOsClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOsClientes);

        txtOsCliId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOsCliId.setEnabled(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("ID");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Nome");

        txtOsCliNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOsCliNome.setEnabled(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("* Campo Obrigatório");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtOsCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOsCliId, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOsCliNome)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(txtOsCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtOsCliId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtOsCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(jLabel7)
        );

        cboOsSituacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboOsSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrada", "Na bancada", "Entrega OK", "Orçamento Reprovado", "Aguardando Aprovação", "Aguardando peças", "Retornou" }));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("*Equipamento");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("*Defeito");

        txtOsDefeito.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("*Serviço");

        txtOsServico.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnOsCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/create.png"))); // NOI18N
        btnOsCreate.setToolTipText("Adicionar");
        btnOsCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsCreateActionPerformed(evt);
            }
        });

        btnOsDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/delete.png"))); // NOI18N
        btnOsDelete.setToolTipText("Apagar");
        btnOsDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel12)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboOsSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtOsEquipamento))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addGap(52, 52, 52)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtOsServico, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                                .addComponent(txtOsDefeito)
                                .addComponent(txtOsTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(31, 52, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(btnOsCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(btnOsRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnOsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btnOsDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnOsPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(cboOsSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtOsEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtOsDefeito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtOsServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtOsTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOsPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOsReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsReadActionPerformed
        pesquisarOs();
    }//GEN-LAST:event_btnOsReadActionPerformed

    private void btnOsEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsEditActionPerformed
        alterar();
    }//GEN-LAST:event_btnOsEditActionPerformed

    private void btnOsPrinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsPrinterActionPerformed
        imprimir();
    }//GEN-LAST:event_btnOsPrinterActionPerformed

    private void txtOsCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOsCliPesquisarKeyReleased
        pesquisarCliente();
    }//GEN-LAST:event_txtOsCliPesquisarKeyReleased

    private void tblOsClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOsClientesMouseClicked
        setarCamposCliente();
    }//GEN-LAST:event_tblOsClientesMouseClicked

    private void txtOsCliPesquisarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtOsCliPesquisarMouseClicked
        controleBotoes(true);
        limparCampos();
    }//GEN-LAST:event_txtOsCliPesquisarMouseClicked

    private void btnOsCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsCreateActionPerformed
        adicionar();
    }//GEN-LAST:event_btnOsCreateActionPerformed

    private void btnOsDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsDeleteActionPerformed
        excluir();
    }//GEN-LAST:event_btnOsDeleteActionPerformed

    private void rbtOsOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOsOrcActionPerformed
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtOsOrcActionPerformed

    private void rbtOsOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOsOsActionPerformed
        tipo = "OS";
    }//GEN-LAST:event_rbtOsOsActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        rbtOsOrc.setSelected(true);
        tipo = "Orçamento";
    }//GEN-LAST:event_formInternalFrameOpened


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgOS;
    private javax.swing.JButton btnOsCreate;
    private javax.swing.JButton btnOsDelete;
    private javax.swing.JButton btnOsEdit;
    private javax.swing.JButton btnOsPrinter;
    private javax.swing.JButton btnOsRead;
    private javax.swing.JComboBox<String> cboOsSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtOsOrc;
    private javax.swing.JRadioButton rbtOsOs;
    private javax.swing.JTable tblOsClientes;
    private javax.swing.JTextField txtOsCliId;
    private javax.swing.JTextField txtOsCliNome;
    private javax.swing.JTextField txtOsCliPesquisar;
    private javax.swing.JTextField txtOsData;
    private javax.swing.JTextField txtOsDefeito;
    private javax.swing.JTextField txtOsEquipamento;
    private javax.swing.JTextField txtOsId;
    private javax.swing.JTextField txtOsServico;
    private javax.swing.JTextField txtOsTecnico;
    private javax.swing.JTextField txtOsValor;
    // End of variables declaration//GEN-END:variables
}
