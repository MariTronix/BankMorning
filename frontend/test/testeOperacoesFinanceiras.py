from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import time

# --- CONFIGURAÇÕES ---
EMAIL_LOGIN = "m@email.com"
SENHA_LOGIN = "123"
CONTA_PROPRIA = "11434"
CONTA_DESTINO = "71207" # Conta do David ou outro colega
URL_BASE = "http://localhost:5173/" 

# --- FUNÇÕES ---
def input_por_placeholder(wait, texto, valor):
    xpath = f"//input[contains(@placeholder, '{texto}')]"
    elem = wait.until(EC.visibility_of_element_located((By.XPATH, xpath)))
    elem.clear()
    elem.send_keys(valor)

def clicar_botao_texto(wait, texto_parcial):
    # Clica em botões que contêm texto (ex: "Confirmar")
    xpath = f"//button[contains(text(), '{texto_parcial}')]"
    elem = wait.until(EC.element_to_be_clickable((By.XPATH, xpath)))
    elem.click()

# --- SETUP DO NAVEGADOR ---
chrome_options = Options()
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)
driver.set_window_size(1920, 1080)
wait = WebDriverWait(driver, 10)

try:
    print("=== INICIANDO TESTE SIMPLIFICADO ===")
    
    # ---------------------------------------------------------
    # 1. LOGIN
    # ---------------------------------------------------------
    print(f"\n[1] Logando como {EMAIL_LOGIN}...")
    driver.get(URL_BASE)
    
    wait.until(EC.visibility_of_element_located((By.ID, 'email'))).send_keys(EMAIL_LOGIN)
    driver.find_element(By.ID, 'password').send_keys(SENHA_LOGIN)
    driver.find_element(By.CLASS_NAME, 'login-button').click()
    
    # Espera carregar a Home verificando se aparece o Saldo
    wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "balance-card")))
    print("   -> Login realizado com sucesso!")

    # ---------------------------------------------------------
    # 2. DEPÓSITO (Erro + Sucesso)
    # ---------------------------------------------------------
    print("\n[2] Testando DEPÓSITO...")
    driver.get(f"{URL_BASE}deposito") # Vai direto pra tela
    
    # CENÁRIO A: ERRO (Valor Negativo)
    print("   [A] Tentando valor negativo...")
    input_por_placeholder(wait, "Número da Conta", CONTA_PROPRIA)
    input_por_placeholder(wait, "Valor", "-50")
    clicar_botao_texto(wait, "Confirmar")
    
    # Verifica se deu erro (não pode ter redirecionado)
    time.sleep(1)
    if "deposito" in driver.current_url:
        print("      -> OK: Sistema não aceitou e continuou na tela.")
    
    # CENÁRIO B: SUCESSO
    print(f"   [B] Depositando R$ 1000,00 na conta {CONTA_PROPRIA}...")
    driver.refresh() # Limpa a tela
    
    input_por_placeholder(wait, "Número da Conta", CONTA_PROPRIA)
    input_por_placeholder(wait, "Valor", "1000")
    clicar_botao_texto(wait, "Confirmar")
    
    # Verifica mensagem de sucesso
    msg = wait.until(EC.visibility_of_element_located((By.TAG_NAME, "p")))
    print(f"      -> Mensagem: {msg.text}")
    
    time.sleep(2) # Espera voltar pra home

    # ---------------------------------------------------------
    # 3. TRANSFERÊNCIA (Erro + Sucesso)
    # ---------------------------------------------------------
    print("\n[3] Testando TRANSFERÊNCIA...")
    # Importante: A rota correta no seu App.jsx é /transacao
    driver.get(f"{URL_BASE}transacao") 
    
    # CENÁRIO A: ERRO (Conta Inexistente)
    print("   [A] Tentando transferir para conta fantasma 00000...")
    input_por_placeholder(wait, "Ex: 123456", "00000")
    input_por_placeholder(wait, "Ex: 50.00", "100")
    clicar_botao_texto(wait, "Confirmar")
    
    msg_erro = wait.until(EC.visibility_of_element_located((By.TAG_NAME, "p")))
    print(f"      -> Mensagem de Erro: {msg_erro.text}")
    
    # CENÁRIO B: SUCESSO
    print(f"   [B] Transferindo R$ 100,00 para conta {CONTA_DESTINO}...")
    driver.refresh()
    
    input_por_placeholder(wait, "Ex: 123456", CONTA_DESTINO)
    input_por_placeholder(wait, "Ex: 50.00", "100")
    clicar_botao_texto(wait, "Confirmar")
    
    msg_sucesso = wait.until(EC.visibility_of_element_located((By.TAG_NAME, "p")))
    print(f"      -> Mensagem: {msg_sucesso.text}")
    
    time.sleep(2)

    # ---------------------------------------------------------
    # 4. SAQUE (Erro + Sucesso)
    # ---------------------------------------------------------
    print("\n[4] Testando SAQUE...")
    driver.get(f"{URL_BASE}saque")
    
    # CENÁRIO A: ERRO (Valor Zero)
    print("   [A] Tentando sacar R$ 0,00...")
    input_por_placeholder(wait, "Valor", "0")
    clicar_botao_texto(wait, "Confirmar")
    
    # Validando se bloqueou (url continua a mesma)
    if "saque" in driver.current_url:
        print("      -> OK: Sistema bloqueou saque zerado.")

    # CENÁRIO B: SUCESSO
    print("   [B] Sacando R$ 100,00...")
    driver.refresh()
    
    input_por_placeholder(wait, "Valor", "100")
    clicar_botao_texto(wait, "Confirmar")
    
    msg = wait.until(EC.visibility_of_element_located((By.TAG_NAME, "p")))
    print(f"      -> Mensagem: {msg.text}")
    
    time.sleep(2)
    
    print("\n=== SUCESSO! TODAS AS OPERAÇÕES FORAM TESTADAS ===")

except Exception as e:
    print(f"\nERRO NO TESTE: {e}")
    driver.save_screenshot("erro_operacoes.png")

finally:
    driver.quit()