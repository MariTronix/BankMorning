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
@Transactional
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer idUsuarioExistente;

    @BeforeEach
    void setup() {
        // Criando usdario
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste Controller");
        usuario.setCpf("999.888.777-66"); // CPF fixo de teste
        usuario.setEmail("controller@teste.com");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setRole(Role.ROLE_USUARIO);

        Usuario salvo = usuarioRepository.save(usuario);
        idUsuarioExistente = salvo.getIdUsuario();
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve atualizar usuário com sucesso quando autenticado")
    @WithMockUser(roles = "USUARIO")
    void atualizar_Sucesso() throws Exception {
        // Dados novos para atualização
        UsuarioRequest request = new UsuarioRequest(
                "Nome Atualizado",
                "novo.email@teste.com",
                LocalDate.of(2000, 5, 20)
        );

        // Teste
        mockMvc.perform(put("/api/usuarios/" + idUsuarioExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"))
                .andExpect(jsonPath("$.email").value("novo.email@teste.com"));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve retornar 403 Forbidden se não estiver logado")
    void atualizar_SemLogin_DeveFalhar() throws Exception {
        UsuarioRequest request = new UsuarioRequest("Nome", "email", LocalDate.now());

        // Executa o teste
        mockMvc.perform(put("/api/usuarios/" + idUsuarioExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve deletar usuário com sucesso")
    @WithMockUser(roles = "USUARIO")
    void deletar_Sucesso() throws Exception {


        mockMvc.perform(delete("/api/usuarios/" + idUsuarioExistente))
                .andExpect(status().isOk());

        boolean existe = usuarioRepository.existsById(idUsuarioExistente);

    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve retornar 404 se o usuário não existir")
    @WithMockUser(roles = "USUARIO")
    void deletar_NaoEncontrado() throws Exception {
        // Tentar deletar um ID que com certeza não existe
        mockMvc.perform(delete("/api/usuarios/" + 999999))
                .andExpect(status().isNotFound());
    }
}
