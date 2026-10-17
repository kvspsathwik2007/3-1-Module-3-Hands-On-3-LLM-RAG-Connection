
import json
from functools import lru_cache

import numpy as np
from sentence_transformers import SentenceTransformer

from config import DOCUMENTS_FILE, TOP_K


EMBEDDING_MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"


@lru_cache(maxsize=1)
def get_embedding_model():
    """Load the embedding model once and reuse it."""
    return SentenceTransformer(EMBEDDING_MODEL_NAME)


def load_documents():
    """Read and validate documents from the JSON dataset."""
    if not DOCUMENTS_FILE.exists():
        raise FileNotFoundError(
            f"Document file not found: {DOCUMENTS_FILE}"
        )

    with DOCUMENTS_FILE.open("r", encoding="utf-8") as file:
        documents = json.load(file)

    if not isinstance(documents, list):
        raise ValueError("documents.json must contain a JSON list.")

    cleaned = []

    for index, document in enumerate(documents):
        if not isinstance(document, dict):
            continue

        content = str(document.get("content", "")).strip()
        if not content:
            continue

        cleaned.append({
            "id": str(document.get("id", f"doc_{index + 1}")),
            "title": str(document.get("title", "Untitled document")),
            "content": content,
        })

    if not cleaned:
        raise ValueError("No usable documents were found.")

    return cleaned


def retrieve_documents(question: str, top_k: int = TOP_K):
    """Retrieve the highest cosine-similarity document chunks."""
    if not question or not question.strip():
        raise ValueError("Question cannot be empty.")

    if top_k < 1:
        raise ValueError("top_k must be at least 1.")

    documents = load_documents()
    model = get_embedding_model()

    document_texts = [doc["content"] for doc in documents]

    document_vectors = model.encode(
        document_texts,
        convert_to_numpy=True,
        normalize_embeddings=True,
    )

    question_vector = model.encode(
        [question.strip()],
        convert_to_numpy=True,
        normalize_embeddings=True,
    )[0]

    # Normalized vectors allow cosine similarity via dot product.
    scores = np.dot(document_vectors, question_vector)

    ranked_indices = np.argsort(scores)[::-1][:min(top_k, len(documents))]

    results = []

    for index in ranked_indices:
        doc = documents[int(index)]

        results.append({
            "id": doc["id"],
            "title": doc["title"],
            "content": doc["content"],
            "similarity": round(float(scores[index]), 4),
        })

    return results
