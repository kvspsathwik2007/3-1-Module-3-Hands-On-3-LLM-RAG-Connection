
import time

from flask import Flask, jsonify, request
from flask_cors import CORS

from config import TOP_K
from services.llm_service import generate_direct_answer
from services.rag_service import generate_rag_answer
from services.retrieval_service import retrieve_documents


app = Flask(__name__)
CORS(app)
@app.get("/")
def home():
    return jsonify({
        "message": "RAG Assistant API is running",
        "endpoints": [
            "/health",
            "/api/retrieve",
            "/api/compare"
        ]
    })

@app.get("/health")
def health():
    return jsonify({
        "status": "ok",
        "service": "RAG Assistant API",
    })


@app.post("/api/retrieve")
def retrieve():
    data = request.get_json(silent=True) or {}
    question = data.get("question", "")

    if not isinstance(question, str) or not question.strip():
        return jsonify({"error": "A non-empty question is required."}), 400

    try:
        started = time.perf_counter()
        sources = retrieve_documents(question, top_k=TOP_K)
        elapsed_ms = round((time.perf_counter() - started) * 1000, 2)

        return jsonify({
            "question": question.strip(),
            "sources": sources,
            "retrieval_time_ms": elapsed_ms,
        })
    except Exception:
        app.logger.exception("Document retrieval failed")
        return jsonify({"error": "Document retrieval failed."}), 500


@app.post("/api/compare")
def compare():
    data = request.get_json(silent=True) or {}
    question = data.get("question", "")

    if not isinstance(question, str) or not question.strip():
        return jsonify({"error": "A non-empty question is required."}), 400

    question = question.strip()
    result = {
        "question": question,
        "direct": None,
        "rag": None,
    }

    # Direct LLM mode: no retrieved documents are supplied.
    started = time.perf_counter()
    try:
        answer = generate_direct_answer(question)
        result["direct"] = {
            "answer": answer,
            "duration_ms": round(
                (time.perf_counter() - started) * 1000, 2
            ),
        }
    except Exception:
        app.logger.exception("Direct Gemini answer failed")
        result["direct"] = {
            "error": "Direct LLM answer failed. Check backend logs."
        }

    # RAG mode: retrieve relevant documents, then ask Gemini.
    started = time.perf_counter()
    try:
        rag_result = generate_rag_answer(question, top_k=TOP_K)
        result["rag"] = {
            "answer": rag_result["answer"],
            "sources": rag_result["sources"],
            "duration_ms": round(
                (time.perf_counter() - started) * 1000, 2
            ),
        }
    except Exception:
        app.logger.exception("RAG answer failed")
        result["rag"] = {
            "error": "RAG answer failed. Check backend logs.",
            "sources": [],
        }

    if result["direct"] is None or "error" in result["direct"]:
        if "error" in result["rag"]:
            return jsonify(result), 502

    if "error" in result["direct"] and "error" in result["rag"]:
        return jsonify(result), 502

    return jsonify(result)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
