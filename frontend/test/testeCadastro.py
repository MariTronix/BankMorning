from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.keys import Keys
import time
import random

# --- 1. FUNÇÕES AUXILIARES ---

def gerar_cpf_valido():
    """Gera um CPF matematicamente válido para passar no Backend Java"""
    def calcula(fatia):
        soma = sum(int(char) * (len(fatia) + 1 - i) for i, char in enumerate(fatia))
        resto = 11 - (soma % 11)
        return '0' if resto > 9 else str(resto)
    base = ''.join([str(random.randint(0, 9)) for _ in range(9)])
    return f"{base[:3]}.{base[3:6]}.{base[6:]}-{calcula(base)}{calcula(base + calcula(base))}"

def interagir_campo(driver, wait, id_campo, valor):
    """
    Função blindada: Rola até o campo, clica, limpa (Ctrl+A -> Del) e digita.
    Evita erros de 'element not interactable'.
    """
    try:
        # 1. Espera existir
        elemento = wait.until(EC.presence_of_element_located((By.ID, id_campo)))
        
        # 2. Rola a tela (block='center' deixa o elemento no meio da tela)
        driver.execute_script("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", elemento)
        time.sleep(0.3) # Pequena pausa para o scroll terminar
        
        # 3. Clica
        try:
            elemento.click()
        except:
            driver.execute_script("arguments[0].click();", elemento)
            
        # 4. Limpa e Digita
        elemento.send_keys(Keys.CONTROL + "a")
        elemento.send_keys(Keys.DELETE)
        elemento.clear() # Garantia extra
        
        if valor:
            elemento.send_keys(valor)
            
    except Exception as e:
        print(f"   [ERRO DE INTERAÇÃO] Campo ID '{id_campo}': {e}")
        raise e

# --- 2. CONFIGURAÇÃO DO DRIVER ---
chrome_options = Options()
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')

servico = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=servico, options=chrome_options)

try:
    url_base = "http://localhost:5173/" 
    print("--- INICIANDO TESTE DE CADASTRO (VERSÃO FINAL) ---")
    
    driver.set_window_size(1920, 1080)
    driver.get(url_base)
    wait = WebDriverWait(driver, 10)

    # --- NAVEGAÇÃO INICIAL ---
    print("-> Navegando para a tela de Cadastro...")
    try:
        btn_ir_cadastro = wait.until(EC.element_to_be_clickable((By.XPATH, "//*[contains(text(), 'Cadastr') or contains(text(), 'Criar')]")))
        btn_ir_cadastro.click()
        time.sleep(1)
    except:
        pass # Assume que já está na tela ou url direta

    # Mapa de IDs (Facilita manutenção)
    IDS = {
        'nome': 'nome',
        'email': 'email',
        'cpf': 'cpf',
        'nasc': 'birthDate',
        'senha': 'password',
        'confirma': 'confirmPassword',
        'botao': 'register-button' # Classe CSS
    }

    # =========================================================
    # CENÁRIO 1: Campos Vazios
    # =========================================================
    print("\n1. Testando Cadastro Vazio...")
    
    btn_cadastro = driver.find_element(By.CLASS_NAME, IDS['botao'])
    driver.execute_script("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn_cadastro)
    time.sleep(0.5)
    btn_cadastro.click()
    
    # Validação: Se a URL não mudou, sucesso.
    if "login" not in driver.current_url: 
        print("   -> SUCESSO: O sistema impediu o envio vazio.")
    else:
        print("   -> FALHA: O sistema redirecionou para login!")

    time.sleep(1)

    # =========================================================
    # CENÁRIO 2: Senhas Diferentes (Lógica Atualizada)
    # =========================================================
    print("\n2. Testando Senhas Diferentes...")
    
    # Preenche dados inválidos
    interagir_campo(driver, wait, IDS['nome'], "Teste Senha Errada")
    interagir_campo(driver, wait, IDS['email'], "erro@senha.com")
    interagir_campo(driver, wait, IDS['cpf'], "000.000.000-00")
    
    # Senhas distintas
    interagir_campo(driver, wait, IDS['senha'], "123456")
    interagir_campo(driver, wait, IDS['confirma'], "999999") 
    
    # Captura URL ANTES de clicar
    url_antes = driver.current_url
    print(f"   -> URL Antes: {url_antes}")

    # Clica
    btn_cadastro.click()
    time.sleep(1)
    
    # Captura URL DEPOIS de clicar
    url_depois = driver.current_url
    
    # Validação Lógica (A URL mudou?)
    if url_antes == url_depois:
        print("   -> SUCESSO LÓGICO: A URL permaneceu a mesma (Bloqueio funcionou).")
        # Tenta achar mensagem de erro visual (Bônus)
        try:
            erro = driver.find_element(By.CSS_SELECTOR, ".error")
            print(f"   -> Mensagem visual: '{erro.text}'")
        except:
            pass
    else:
        print(f"   -> FALHA: O sistema mudou de página para: {url_depois}")

    # =========================================================
    # CENÁRIO 3: Cadastro Válido (Caminho Feliz)
    # =========================================================
    print("\n3. Testando Cadastro Válido (Dados Reais)...")
    
    # Gerar dados novos
    cpf_novo = gerar_cpf_valido()
    email_novo = f"cliente.final.{random.randint(1000,9999)}@sucesso.com"
    senha_forte = "Teste@1234"
    
    print(f"   -> Cadastrando: {email_novo}")

    # Preencher (A função interagir_campo já limpa antes de digitar)
    interagir_campo(driver, wait, IDS['nome'], "Usuario QA Final")
    interagir_campo(driver, wait, IDS['email'], email_novo)
    interagir_campo(driver, wait, IDS['cpf'], cpf_novo)
    interagir_campo(driver, wait, IDS['nasc'], "01012000")
    interagir_campo(driver, wait, IDS['senha'], senha_forte)
    interagir_campo(driver, wait, IDS['confirma'], senha_forte)

    # Clicar
    print("   -> Enviando formulário...")
    driver.execute_script("arguments[0].click();", btn_cadastro)

    # Esperar Alerta de Sucesso
    print("   -> Aguardando confirmação...")
    try:
        wait.until(EC.alert_is_present())
        alerta = driver.switch_to.alert
        print(f"   -> SUCESSO TOTAL! Alerta do Sistema: '{alerta.text}'")
        alerta.accept()
    except Exception as e:
        # Se não tiver alerta, procura erro na tela
        try:
            erro_tela = driver.find_element(By.CSS_SELECTOR, ".error")
            print(f"   -> FALHA NO BACKEND: {erro_tela.text}")
        except:
            print("   -> ERRO: Timeout aguardando resposta.")
        driver.save_screenshot("erro_cadastro_final.png")

    time.sleep(2)
    print("\n>>> TESTE FINALIZADO <<<")

except Exception as e:
    print(f"ERRO FATAL NO SCRIPT: {e}")
    driver.save_screenshot("erro_fatal.png")

finally:
    driver.quit()