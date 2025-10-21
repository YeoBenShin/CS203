const fetchApi = async (token, path, method = "GET", body = null) => {
  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080";

  const options = {
    method,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  };

  // Only attach body for methods that support it (POST, PUT, PATCH)
  if (body && method !== "GET" && method !== "HEAD") {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(`${baseUrl}/${path}`, options);
  return response;
};

export default fetchApi;