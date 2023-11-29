
from mysql.connector import connect
import psutil
import platform
import time
import mysql.connector
from datetime import datetime
import ping3
import json
import requests
import pymssql

#alerta = {"text": "alerta"}

webhook = "https://hooks.slack.com/services/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"
#requests.post(webhook, data=json.dumps(alerta))


idRobo = 1

#descomente abaixo quando for ora criar esse arquivo peo kotlin
# idRobo = #${roboId}



def mysql_connection(host, user, passwd, database=None):
    connection = connect(
        host=host,
        user=user,
        passwd=passwd,
        database=database
    )
    return connection

def bytes_para_gb(bytes_value):
    return bytes_value / (1024 ** 3)

def milissegundos_para_segundos(ms_value):
    return ms_value / 1000

#connection = mysql_connection('localhost', 'root', '123456', 'medconnect')
sqlserver_connection = pymssql.connect(server='52.7.105.138', database='medconnect', user='sa', password='medconnect123');


#Disco

meu_so = platform.system()
if(meu_so == "Linux"):
    nome_disco = '/'
    disco = psutil.disk_usage(nome_disco)
elif(meu_so == "Windows"):
    nome_disco = 'C:\\'
disco = psutil.disk_usage(nome_disco)
discoPorcentagem = disco.percent

ins = [discoPorcentagem]
componentes = [10]

horarioAtual = datetime.now()
horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

#cursor = connection.cursor()
server_cursor = sqlserver_connection.cursor()
    
for i in range(len(ins)):
        
    dado = ins[i]
        
    componente = componentes[i]
    
    query = "INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)"
    
    #cursor.execute(query, (dado, idRobo, componente, horarioFormatado))
    server_cursor.execute(query, (dado, idRobo, componente, horarioFormatado))



print("\nDisco porcentagem:", discoPorcentagem)


while True:

    #CPU
    cpuPorcentagem = psutil.cpu_percent(None)
    
    if(cpuPorcentagem > 60 and cpuPorcentagem > 70):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(cpuPorcentagem > 70 and cpuPorcentagem > 80):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))
    if(cpuPorcentagem > 80):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
        



    
    #Memoria
    memoriaPorcentagem = psutil.virtual_memory()[2]
   
    if(memoriaPorcentagem > 60 and memoriaPorcentagem > 70):
        alerta = {"text": f"⚠️  Alerta na ram da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(memoriaPorcentagem > 70 and memoriaPorcentagem > 80):
        alerta = {"text": f"⚠️  Alerta na ram da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))  
    if(memoriaPorcentagem > 80):
        alerta = {"text": f" ⚠️  Alerta na ram da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
   
    #Rede
        
    destino = "google.com"  
    latencia = ping3.ping(destino) * 1000
    if(latencia > 40 and latencia > 60):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(latencia > 60 and latencia > 80):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))
    if(latencia > 80):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
    
    if latencia is not None:
        print(f"Latência para {destino}: {latencia:.2f} ms")
    else:
        print(f"Não foi possível alcançar {destino}")

    #Outros
    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime("%Y-%m-%d %H:%M:%S")
    

    horarioAtual = datetime.now()
    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')
    
    ins = [cpuPorcentagem, memoriaPorcentagem, latencia]
    componentes = [1, 5, 16]
    
    #cursor = connection.cursor()
    server_cursor = sqlserver_connection.cursor()
    
    
    for i in range(len(ins)):
        dado = ins[i]
        componente = componentes[i]

        query = "INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)"

        #cursor.execute(query, (dado, idRobo, componente, horarioFormatado))
        server_cursor.execute(query, (dado, idRobo, componente, horarioFormatado))

        #connection.commit()
        sqlserver_connection.commit()
       
    print("\nINFORMAÇÕES SOBRE PROCESSAMENTO: ")
    print('\nPorcentagem utilizada da CPU: ',cpuPorcentagem,
          '\nPorcentagem utilizada de memoria: ', memoriaPorcentagem)
   
    
       


    time.sleep(5)

#cursor.close()
#connection.close()
server_cursor.close()
sqlserver_connection.close()
    
