
import os

source_file = r"d:\projects\worry-free-road-research\append_sections_52_53.md"
target_file = r"d:\projects\worry-free-road-research\材料\初稿.md"

try:
    with open(source_file, "r", encoding="utf-8") as f:
        content = f.read()

    with open(target_file, "a", encoding="utf-8") as f:
        f.write("\n\n" + content)
    
    print("Successfully appended content.")

except Exception as e:
    print(f"Error appending content: {e}")
