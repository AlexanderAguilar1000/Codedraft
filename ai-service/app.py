import os 

from dotenv import load_dotenv
from groq import Groq
from flask import Flask, jsonify



load_dotenv()  #Busca el archivo .env y carga sus variables en las variables de entorno del proceso . 

#Obtengo las variables de entorno del archivo ".env"
api_key=os.getenv("GROP_API_KEY")
model=os.getenv("GROP_MODEL")

#Restricciones , si no encuentra las variables de entorno 
if not api_key:
    raise RuntimeError("GROQ_API_KEY no está configurada")

if not model:
    raise RuntimeError("GROQ_MODEL no está configurado")

#Me conecto a Groq  
client = Groq(api_key=api_key)


#Pruebo endpoind en Flask
app = Flask(__name__)

@app.get("/health")
def health():
    return jsonify({"status": "UP"})


if __name__=="__main__":
    app.run(port=5000,debug=True)


#response = client.chat.completions.create(
#   model=model,
# messages=[
#      {
#         "role": "user",
#         "content": "Hola, responde brevemente."
#     }
#  ]
#)

#print(response.choices[0].message.content)


