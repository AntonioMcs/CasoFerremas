import urllib.request
import json

url = "https://search.maven.org/solrsearch/select?q=g:%22com.transbank%22+AND+a:%22transbank-sdk%22&rows=20&wt=json"
with urllib.request.urlopen(url, timeout=20) as response:
    data = response.read().decode("utf-8")
obj = json.loads(data)
print(obj["response"]["numFound"])
for doc in obj["response"]["docs"]:
    print(doc.get("g"), doc.get("a"), doc.get("latestVersion"), doc.get("v"))
