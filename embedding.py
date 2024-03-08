from FlagEmbedding import FlagModel
from flask import Flask, request

app = Flask(__name__)

model = FlagModel('./BAAI/bge-large-zh-v1.5',
                  query_instruction_for_retrieval="为这个句子生成表示以用于检索相关文章：",
                  use_fp16=True)  # Setting use_fp16 to True speeds up computation with a slight performance degradation


@app.route('/embedding', methods=['GET'])
def hello_world():
    sentence = request.args.get('sentence', '')

    embeddings_1 = model.encode(sentence).tolist()

    return embeddings_1


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
