import { useEffect, useState } from "react";
import "./App.css";

export default function App() {
  const [boards, setBoards] = useState([]);
  const [currentBoardId, setCurrentBoardId] = useState(null);
  const [posts, setPosts] = useState([]);
  const [err, setErr] = useState("");

  // 1) 게시판 목록
  useEffect(() => {
    (async () => {
      try {
        setErr("");
        const res = await fetch("/api/boards", { credentials: "include" });
        if (!res.ok) {
          setErr(`boards 호출 실패: ${res.status} (로그인 필요할 수 있음)`);
          return;
        }
        const data = await res.json();
        setBoards(data);

        // 기본 게시판 선택
        if (data.length > 0) setCurrentBoardId(data[0].id);
      } catch (e) {
        setErr(`boards 에러: ${String(e)}`);
      }
    })();
  }, []);

  // 2) 게시글 목록
  useEffect(() => {
    if (!currentBoardId) return;
    (async () => {
      try {
        setErr("");
        const res = await fetch(`/api/posts?boardId=${currentBoardId}`, {
          credentials: "include",
        });
        if (!res.ok) {
          setErr(`posts 호출 실패: ${res.status}`);
          return;
        }
        const data = await res.json();
        setPosts(data);
      } catch (e) {
        setErr(`posts 에러: ${String(e)}`);
      }
    })();
  }, [currentBoardId]);

  return (
    <div style={{ maxWidth: 900, margin: "24px auto", padding: 16 }}>
      <h2 style={{ marginBottom: 12 }}>게시판</h2>

      {err && (
        <div style={{ marginBottom: 12, color: "crimson" }}>
          {err}
          <div style={{ fontSize: 12, marginTop: 6 }}>
            ※ 401이면 Spring 쪽 로그인 세션이 없는 상태일 가능성이 큼
          </div>
        </div>
      )}

      {/* 게시판 탭 */}
      <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 16 }}>
        {boards.map((b) => (
          <button
            key={b.id}
            onClick={() => setCurrentBoardId(b.id)}
            style={{
              padding: "8px 12px",
              borderRadius: 10,
              border: "1px solid #ddd",
              cursor: "pointer",
              fontWeight: b.id === currentBoardId ? 700 : 400,
            }}
          >
            {b.name}
          </button>
        ))}
      </div>

      {/* 게시글 목록 */}
      <h3 style={{ margin: "12px 0" }}>게시글 목록</h3>

      <div style={{ marginBottom: 12 }}>
        <a href={`/posts/new?boardId=${currentBoardId}`}>글쓰기(기존 화면)</a>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th style={{ textAlign: "left", borderBottom: "1px solid #eee", padding: 8 }}>제목</th>
            <th style={{ textAlign: "left", borderBottom: "1px solid #eee", padding: 8 }}>작성자</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((p) => (
            <tr key={p.id}>
              <td style={{ borderBottom: "1px solid #f3f3f3", padding: 8 }}>
                <a href={`/posts/${p.id}`}>{p.title}</a>
              </td>
              <td style={{ borderBottom: "1px solid #f3f3f3", padding: 8 }}>
                {p.authorName}
              </td>
            </tr>
          ))}
          {posts.length === 0 && (
            <tr>
              <td colSpan={2} style={{ padding: 12, color: "#666" }}>
                게시글이 없거나(또는 권한상 조회 불가) 로딩 중일 수 있어요.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}