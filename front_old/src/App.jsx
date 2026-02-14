useEffect(() => {
  if (boardId == null) return; // null/undefined만 차단
  (async () => {
    setError("");
    const res = await fetch(`/api/posts?boardId=${boardId}`, { credentials: "include" });

    if (res.status === 401) {
      setError("로그인이 필요합니다.");
      return;
    }

    const data = await res.json();
    setPosts(data);
  })();
}, [boardId]);