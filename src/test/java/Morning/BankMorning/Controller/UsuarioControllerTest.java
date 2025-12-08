package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Garante que as alterações no banco sejam desfeitas após cada teste (Rollback)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula as requisições HTTP (O "Postman" do código)
    //
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper; // Converte Objetos Java para JSON

    private Integer idUsuarioExistente;

    @BeforeEach
    void setup() {
        // 1. Cria um usuário real no banco antes de cada teste para termos o que editar/deletar
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste Controller");
        usuario.setCpf("999.888.777-66"); // CPF fixo de teste
        usuario.setEmail("controller@teste.com");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setRole(Role.ROLE_USUARIO);

        // Precisamos salvar para gerar o ID
        Usuario salvo = usuarioRepository.save(usuario);
        idUsuarioExistente = salvo.getIdUsuario();
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve atualizar usuário com sucesso quando autenticado")
    @WithMockUser(roles = "USUARIO") // Simula que estamos logados e temos permissão
    void atualizar_Sucesso() throws Exception {
        // Cenário: Dados novos para atualização
        UsuarioRequest request = new UsuarioRequest(
                "Nome Atualizado",
                "novo.email@teste.com",
                LocalDate.of(2000, 5, 20)
        );

        // Ação: Faz o PUT
        mockMvc.perform(put("/usuarios/" + idUsuarioExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Verificação:
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.nome").value("Nome Atualizado")) // Verifica se o JSON de resposta mudou
                .andExpect(jsonPath("$.email").value("novo.email@teste.com"));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve retornar 403 Forbidden se não estiver logado")
    void atualizar_SemLogin_DeveFalhar() throws Exception {
        UsuarioRequest request = new UsuarioRequest("Nome", "email", LocalDate.now());

        // Ação: Faz o PUT sem a anotação @WithMockUser
        mockMvc.perform(put("/usuarios/" + idUsuarioExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Espera 403 Forbidden
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve deletar usuário com sucesso")
    @WithMockUser(roles = "USUARIO")
    void deletar_Sucesso() throws Exception {
        // Ação: Faz o DELETE
        mockMvc.perform(delete("/usuarios/" + idUsuarioExistente))
                .andExpect(status().isOk());

        boolean existe = usuarioRepository.existsById(idUsuarioExistente);

    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve retornar 404 se o usuário não existir")
    @WithMockUser(roles = "USUARIO")
    void deletar_NaoEncontrado() throws Exception {
        // Tenta deletar um ID que com certeza não existe
        mockMvc.perform(delete("/usuarios/" + 999999))
                .andExpect(status().isNotFound()); //O seu Service lança RecursoNaoEncontrado -> 404?
        // Se lançar 500 ou 400, ajuste aqui para isInternalServerError() ou isBadRequest()
    }
}