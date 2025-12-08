from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import time

# Configurações do navegador
chrome_options = Options()
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')

# Inicializa o driver automaticamente
servico = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=servico, options=chrome_options)

try:
    url = "http://localhost:5173/" 
    print("--- INICIANDO BATERIA DE TESTES ---")
    
    driver.maximize_window()
    driver.get(url)
    wait = WebDriverWait(driver, 10)

    # Mapeando os elementos (fazemos isso uma vez para usar várias vezes)
    campo_usuario = wait.until(EC.presence_of_element_located((By.ID, 'email')))
    campo_senha = wait.until(EC.presence_of_element_located((By.ID, 'password')))
    botao_entrar = wait.until(EC.element_to_be_clickable((By.CLASS_NAME, 'login-button')))

    # ---------------------------------------------------------
    # CENÁRIO 1: Campos Vazios (Validação Obrigatória)
    # ---------------------------------------------------------
    print("0. Testando Botão com Campos Vazios...")
    
    # Garantir que estão limpos
    campo_usuario.clear()
    campo_senha.clear()
    
    # Clicar sem digitar nada
    botao_entrar.click()
    
    # VERIFICAÇÃO INTELIGENTE:
    # Se o sistema for bem feito, a URL não deve mudar (continua na login)
    # Ou deve aparecer um alerta. Vamos checar se a URL mudou.
    url_atual = driver.current_url
    if url_atual == url:
        print("   -> SUCESSO: O sistema impediu o login vazio.")
    else:
        print("   -> FALHA: O sistema deixou entrar ou mudou de página!")
    
    time.sleep(2)



    # ---------------------------------------------------------
    # CENÁRIO 2: Email Inválido / Não Cadastrado
    # ---------------------------------------------------------
    print("1. Testando Email Inexistente...")
    campo_usuario.send_keys('naoexiste@banco.com')
    campo_senha.send_keys('123456') # A senha não importa aqui
    botao_entrar.click()
    
    # Pausa para você ver a mensagem de erro na tela
    time.sleep(3) 
    print("   -> Erro visualizado.")

    # Limpeza para o próximo teste
    campo_usuario.clear()
    campo_senha.clear()
    time.sleep(1)

    # ---------------------------------------------------------
    # CENÁRIO 3: Email Correto + Senha Errada
    # ---------------------------------------------------------
    print("2. Testando Senha Incorreta...")
    campo_usuario.send_keys('m@email.com') # Email válido
    campo_senha.send_keys('senhaerrada')
    botao_entrar.click()

    time.sleep(3)
    print("   -> Bloqueio visualizado.")

    # Limpeza
    campo_senha.clear() # Só precisamos limpar a senha, o email já está certo
    time.sleep(1)

    # ---------------------------------------------------------
    # CENÁRIO 4: Login com Sucesso (Caminho Feliz)
    # ---------------------------------------------------------
    print("3. Testando Credenciais Válidas...")
    # O email já está preenchido do passo anterior
    campo_senha.send_keys('123') # Senha correta
    botao_entrar.click()

    # Espera maior para ver o redirecionamento (Dashboard)
    time.sleep(5)
    print(">>> SUCESSO! Todos os passos executados.")

except Exception as e:
    print(f"ERRO: O teste parou inesperadamente: {e}")
    
finally:
    print("Fechando navegador...")
    driver.quit()