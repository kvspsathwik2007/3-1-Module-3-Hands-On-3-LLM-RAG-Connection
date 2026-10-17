
import os
from pathlib import Path

from dotenv import load_dotenv

# Load backend/.env regardless of the current working directory.
BACKEND_DIR = Path(__file__).resolve().parent
load_dotenv(BACKEND_DIR / ".env")

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "").strip()
GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-flash-latest").strip()

DATA_DIR = BACKEND_DIR / "data"
DOCUMENTS_FILE = DATA_DIR / "documents.json"

TOP_K = int(os.getenv("TOP_K", "3"))

if TOP_K < 1:
    raise ValueError("TOP_K must be at least 1")
