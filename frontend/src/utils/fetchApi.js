const stripSlashes = (s = "") => String(s).replace(/^\/+|\/+$/g, "");

const fetchApi = async (token, path, method = "GET", body = null) => {
  const cleanedPath = stripSlashes(path);
  const url = `/backapi/${cleanedPath}`; // same-origin via rewrite

  const headers = { "Content-Type": "application/json" };
  if (token) headers.Authorization = `Bearer ${token}`;

  const options = { method, headers };
  if (body && method !== "GET" && method !== "HEAD") options.body = JSON.stringify(body);

  const res = await fetch(url, options);
  return res;
};

export default fetchApi;