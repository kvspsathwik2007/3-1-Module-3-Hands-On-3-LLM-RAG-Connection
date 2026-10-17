
from functools import lru_cache

from google import genai

from config import GEMINI_API_KEY, GEMINI_MODEL


@lru_cache(maxsize=1)
def get_gemini_client():
    """Create the Gemini client only when first needed."""
    if not GEMINI_API_KEY:
        raise RuntimeError(
            "GEMINI_API_KEY is missing. Add it to backend/.env."
        )

    return genai.Client(api_key=GEMINI_API_KEY)


def generate_answer(prompt: str) -> str:
    """Generate a text answer using the configured Gemini model."""
    if not prompt or not prompt.strip():
        raise ValueError("Prompt cannot be empty.")

    client = get_gemini_client()

    response = client.models.generate_content(
        model=GEMINI_MODEL,
        contents=prompt.strip(),
    )

    answer = response.text

    if not answer or not answer.strip():
        raise RuntimeError("Gemini returned an empty text response.")

    return answer.strip()


def generate_direct_answer(question: str) -> str:
    """Answer without document retrieval."""
    prompt = (
        "Answer the user's question clearly and accurately. "
        "If you are uncertain, say so.\n\n"
        f"Question:\n{question.strip()}"
    )

    return generate_answer(prompt)
