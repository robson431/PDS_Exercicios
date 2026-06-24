package controller;

import model.Livro;
import model.LivroTableModel;
import View.Cadastro;
import View.JanelaTabela; 
import View.JanelaEditar; // Importa a nova janela de edição

public class LivroController {
    
    private LivroTableModel livroModel;
    private Cadastro viewCadastro;
    
    public LivroController(LivroTableModel modelo, Cadastro view) {
        this.livroModel = modelo;
        this.viewCadastro = view;
        
        this.viewCadastro.getBtnAdicionar().addActionListener(e -> eventoBotaoAdicionar());
        this.viewCadastro.getBtnVisualizar().addActionListener(e -> eventoBotaoVisualizar());
    }
    
    private void eventoBotaoVisualizar() {
        JanelaTabela janelaTabela = new JanelaTabela();
        janelaTabela.getTable().setModel(this.livroModel);
        
        // Configura o botão remover da tabela
        janelaTabela.getBtnRemover().addActionListener(e -> {
            int linhaSelecionada = janelaTabela.getTable().getSelectedRow();
            if (linhaSelecionada != -1) {
                livroModel.remover(linhaSelecionada);
            }
        });

        // NOVO: Configura o botão de Editar da tabela
        janelaTabela.getBtnEditar().addActionListener(e -> {
            int linhaSelecionada = janelaTabela.getTable().getSelectedRow();
            
            // Só faz algo se o usuário tiver clicado/selecionado uma linha
            if (linhaSelecionada != -1) {
                // 1. Pegamos a referência do livro original direto da lista usando o TableModel
                // Como criamos um método getValueAt, para pegar o objeto inteiro nós acessamos a lista do model indiretamente ou criamos um método lá.
                // Para não complicar seu model, podemos pegar os dados da própria linha ou fazer um cast se o model expor o método. 
                // Vamos usar a forma mais limpa criando a JanelaEditar e passando os dados atuais da tabela para os campos:
                
                JanelaEditar janelaEditar = new JanelaEditar();
                
                // Preenche os campos da tela de edição com o que está na tabela atualmente
                janelaEditar.getTxtNome().setText(janelaTabela.getTable().getValueAt(linhaSelecionada, 0).toString());
                janelaEditar.getTxtEditora().setText(janelaTabela.getTable().getValueAt(linhaSelecionada, 1).toString());
                janelaEditar.getTxtAno().setText(janelaTabela.getTable().getValueAt(linhaSelecionada, 2).toString());
                janelaEditar.getTxtAutor().setText(janelaTabela.getTable().getValueAt(linhaSelecionada, 3).toString());
                janelaEditar.getComboBox().setSelectedItem(janelaTabela.getTable().getValueAt(linhaSelecionada, 4));

                // Ação do botão salvar da tela de Edição
                janelaEditar.getBtnSalvar().addActionListener(ev -> {
                    try {
                        // Resgata o livro da memória para atualizar as propriedades dele
                        // Para atualizar sem mexer na estrutura do arraylist, alteramos as colunas da tabela direto:
                        String novoNome = janelaEditar.getTxtNome().getText();
                        String novaEditora = janelaEditar.getTxtEditora().getText();
                        int novoAno = Integer.parseInt(janelaEditar.getTxtAno().getText());
                        String novoAutor = janelaEditar.getTxtAutor().getText();
                        String novoGenero = janelaEditar.getComboBox().getSelectedItem().toString();

                        // Criamos um método simples para re-inserir ou atualizar o livro na lista
                        // Mas o jeito mais fácil com o seu modelo atual é remover o antigo e colocar o atualizado na mesma posição, veja:
                        Livro livroAtualizado = new Livro(novoNome, novaEditora, novoAno, novoAutor, novoGenero);
                        
                        // Atualiza no seu modelo de dados
                        livroModel.remover(linhaSelecionada);
                        livroModel.adicionarLivro(livroAtualizado);
                        
                        // Fecha a janelinha de edição
                        janelaEditar.dispose();
                        
                    } catch (Exception error) {
                        System.out.println("Erro ao atualizar dados: Verifique o campo Ano.");
                    }
                });

                janelaEditar.setVisible(true);
            }
        });
        
        janelaTabela.setVisible(true);
    }

    public void eventoBotaoAdicionar() {
        try {
            String nome = this.viewCadastro.getTxtNome().getText();
            String editora = this.viewCadastro.getTxtEditora().getText();
            String autor = this.viewCadastro.getTxtAutor().getText();
            String genero = this.viewCadastro.getComboBox().getSelectedItem().toString();
            String anoTexto = this.viewCadastro.getTxtAno().getText().trim();
            
            if (anoTexto.isEmpty()) return; 
            
            int anoLancamento = Integer.parseInt(anoTexto);
            
            Livro l = new Livro(nome, editora, anoLancamento, autor, genero);
            livroModel.adicionarLivro(l);
            
            this.viewCadastro.getTxtNome().setText("");
            this.viewCadastro.getTxtEditora().setText("");
            this.viewCadastro.getTxtAno().setText("");
            this.viewCadastro.getTxtAutor().setText("");
            
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar.");
        }
    }
}