#include <windows.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <stdio.h>

#define SIZE 4
#define PAD 12
#define GAP 12
#define CELL 99
#define BOARD_X 12
#define BOARD_Y 150
#define BOARD_SIZE (PAD*2 + CELL*4 + GAP*3)
#define WIN_W (BOARD_SIZE + 24)
#define WIN_H (BOARD_Y + BOARD_SIZE + 24)

static int grid[SIZE][SIZE];
static int score = 0, best = 0;
static int won = 0, over = 0, keepPlaying = 0;

static COLORREF bgColor      = RGB(14, 25, 18);
static COLORREF panelColor   = RGB(23, 37, 30);
static COLORREF emptyColor   = RGB(29, 44, 36);
static COLORREF textColor    = RGB(234, 243, 236);
static COLORREF mutedColor   = RGB(124, 151, 138);
static COLORREF accentColor  = RGB(232, 184, 84);

typedef struct { COLORREF bg; COLORREF fg; } TileStyle;

static TileStyle styleFor(int v) {
    switch (v) {
        case 2:    return (TileStyle){ RGB(217,239,224), RGB(27,46,34) };
        case 4:    return (TileStyle){ RGB(193,230,206), RGB(27,46,34) };
        case 8:    return (TileStyle){ RGB(147,216,171), RGB(18,38,26) };
        case 16:   return (TileStyle){ RGB(99,201,141),  RGB(15,33,23) };
        case 32:   return (TileStyle){ RGB(56,182,115),  RGB(242,251,245) };
        case 64:   return (TileStyle){ RGB(32,158,99),   RGB(242,251,245) };
        case 128:  return (TileStyle){ RGB(23,145,111),  RGB(242,251,245) };
        case 256:  return (TileStyle){ RGB(20,126,147),  RGB(242,251,245) };
        case 512:  return (TileStyle){ RGB(44,99,174),   RGB(242,251,245) };
        case 1024: return (TileStyle){ RGB(106,63,168),  RGB(245,240,255) };
        case 2048: return (TileStyle){ RGB(232,184,84),  RGB(42,29,6) };
        default:   return (TileStyle){ RGB(255,90,60),   RGB(42,13,5) };
    }
}

static void resetGame(void) {
    memset(grid, 0, sizeof(grid));
    score = 0; won = 0; over = 0; keepPlaying = 0;
    for (int i = 0; i < 2; i++) {
        int empty[SIZE*SIZE][2], n = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) { empty[n][0]=r; empty[n][1]=c; n++; }
        if (n == 0) continue;
        int pick = rand() % n;
        grid[empty[pick][0]][empty[pick][1]] = (rand() % 10 == 0) ? 4 : 2;
    }
}

static void addRandomTile(void) {
    int empty[SIZE*SIZE][2], n = 0;
    for (int r = 0; r < SIZE; r++)
        for (int c = 0; c < SIZE; c++)
            if (grid[r][c] == 0) { empty[n][0]=r; empty[n][1]=c; n++; }
    if (n == 0) return;
    int pick = rand() % n;
    grid[empty[pick][0]][empty[pick][1]] = (rand() % 10 == 0) ? 4 : 2;
}

static int slideLine(int *line, int *gain) {
    int temp[SIZE], idx = 0;
    for (int i = 0; i < SIZE; i++) if (line[i] != 0) temp[idx++] = line[i];
    for (int i = idx; i < SIZE; i++) temp[i] = 0;
    for (int i = 0; i < SIZE - 1; i++) {
        if (temp[i] != 0 && temp[i] == temp[i+1]) {
            temp[i] *= 2;
            *gain += temp[i];
            for (int j = i+1; j < SIZE-1; j++) temp[j] = temp[j+1];
            temp[SIZE-1] = 0;
        }
    }
    int moved = 0;
    for (int i = 0; i < SIZE; i++) if (temp[i] != line[i]) moved = 1;
    for (int i = 0; i < SIZE; i++) line[i] = temp[i];
    return moved;
}

static int hasMovesAvailable(void) {
    for (int r = 0; r < SIZE; r++)
        for (int c = 0; c < SIZE; c++) {
            if (grid[r][c] == 0) return 1;
            if (c < SIZE-1 && grid[r][c] == grid[r][c+1]) return 1;
            if (r < SIZE-1 && grid[r][c] == grid[r+1][c]) return 1;
        }
    return 0;
}

static int doMove(int dir) { /* 0=left 1=right 2=up 3=down */
    int moved = 0, gain = 0;
    if (dir == 0 || dir == 1) {
        for (int r = 0; r < SIZE; r++) {
            int line[SIZE];
            for (int c = 0; c < SIZE; c++)
                line[c] = (dir == 0) ? grid[r][c] : grid[r][SIZE-1-c];
            if (slideLine(line, &gain)) moved = 1;
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = (dir == 0) ? line[c] : line[SIZE-1-c];
        }
    } else {
        for (int c = 0; c < SIZE; c++) {
            int line[SIZE];
            for (int r = 0; r < SIZE; r++)
                line[r] = (dir == 2) ? grid[r][c] : grid[SIZE-1-r][c];
            if (slideLine(line, &gain)) moved = 1;
            for (int r = 0; r < SIZE; r++)
                grid[r][c] = (dir == 2) ? line[r] : line[SIZE-1-r];
        }
    }
    if (moved) {
        score += gain;
        if (score > best) best = score;
        addRandomTile();
        if (!won) {
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++)
                    if (grid[r][c] >= 2048) won = 1;
        }
        if (!hasMovesAvailable()) over = 1;
    }
    return moved;
}

static void drawTextCentered(HDC hdc, RECT rect, const char *txt, COLORREF color, int height, int bold) {
    HFONT font = CreateFontA(height, 0, 0, 0, bold ? FW_BOLD : FW_SEMIBOLD, 0, 0, 0,
        DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS,
        CLEARTYPE_QUALITY, DEFAULT_PITCH | FF_SWISS, "Segoe UI");
    HFONT old = (HFONT)SelectObject(hdc, font);
    SetTextColor(hdc, color);
    SetBkMode(hdc, TRANSPARENT);
    DrawTextA(hdc, txt, -1, &rect, DT_CENTER | DT_VCENTER | DT_SINGLELINE);
    SelectObject(hdc, old);
    DeleteObject(font);
}

static void fillRoundRect(HDC hdc, RECT r, COLORREF color, int radius) {
    HBRUSH brush = CreateSolidBrush(color);
    HPEN pen = CreatePen(PS_SOLID, 1, color);
    HBRUSH oldB = (HBRUSH)SelectObject(hdc, brush);
    HPEN oldP = (HPEN)SelectObject(hdc, pen);
    RoundRect(hdc, r.left, r.top, r.right, r.bottom, radius, radius);
    SelectObject(hdc, oldB); SelectObject(hdc, oldP);
    DeleteObject(brush); DeleteObject(pen);
}

static void paint(HWND hwnd, HDC hdc) {
    RECT client; GetClientRect(hwnd, &client);
    HDC mem = CreateCompatibleDC(hdc);
    HBITMAP bmp = CreateCompatibleBitmap(hdc, client.right, client.bottom);
    HBITMAP oldBmp = (HBITMAP)SelectObject(mem, bmp);

    HBRUSH bgBrush = CreateSolidBrush(bgColor);
    FillRect(mem, &client, bgBrush);
    DeleteObject(bgBrush);

    RECT titleRect = {20, 14, 220, 60};
    drawTextCentered(mem, titleRect, "2048", accentColor, 36, 1);
    RECT subRect = {20, 56, 300, 80};
    drawTextCentered(mem, subRect, "Fusionnez les cristaux", mutedColor, 14, 0);

    char scoreBuf[32], bestBuf[32];
    sprintf(scoreBuf, "%d", score);
    sprintf(bestBuf, "%d", best);

    RECT scoreBox = { WIN_W-220, 16, WIN_W-120, 66 };
    fillRoundRect(mem, scoreBox, panelColor, 8);
    RECT scoreLbl = { scoreBox.left, scoreBox.top+4, scoreBox.right, scoreBox.top+22 };
    drawTextCentered(mem, scoreLbl, "SCORE", mutedColor, 11, 1);
    RECT scoreVal = { scoreBox.left, scoreBox.top+20, scoreBox.right, scoreBox.bottom };
    drawTextCentered(mem, scoreVal, scoreBuf, textColor, 20, 1);

    RECT bestBox = { WIN_W-112, 16, WIN_W-12, 66 };
    fillRoundRect(mem, bestBox, panelColor, 8);
    RECT bestLbl = { bestBox.left, bestBox.top+4, bestBox.right, bestBox.top+22 };
    drawTextCentered(mem, bestLbl, "MEILLEUR", mutedColor, 11, 1);
    RECT bestVal = { bestBox.left, bestBox.top+20, bestBox.right, bestBox.bottom };
    drawTextCentered(mem, bestVal, bestBuf, accentColor, 20, 1);

    RECT boardRect = { BOARD_X, BOARD_Y, BOARD_X+BOARD_SIZE, BOARD_Y+BOARD_SIZE };
    fillRoundRect(mem, boardRect, panelColor, 14);

    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int x = BOARD_X + PAD + c*(CELL+GAP);
            int y = BOARD_Y + PAD + r*(CELL+GAP);
            RECT cellRect = { x, y, x+CELL, y+CELL };
            int v = grid[r][c];
            if (v == 0) {
                fillRoundRect(mem, cellRect, emptyColor, 8);
            } else {
                TileStyle st = styleFor(v);
                fillRoundRect(mem, cellRect, st.bg, 8);
                char buf[8]; sprintf(buf, "%d", v);
                int fsize = (v < 100) ? 34 : (v < 1000) ? 28 : 22;
                drawTextCentered(mem, cellRect, buf, st.fg, fsize, 1);
            }
        }
    }

    RECT footRect = { BOARD_X, BOARD_Y+BOARD_SIZE+2, BOARD_X+BOARD_SIZE, BOARD_Y+BOARD_SIZE+22 };
    drawTextCentered(mem, footRect, "Fleches ou WASD  |  N = nouvelle partie", mutedColor, 12, 0);

    if (won && !keepPlaying) {
        RECT overlay = boardRect;
        fillRoundRect(mem, overlay, RGB(14,25,18), 14);
        RECT t1 = { overlay.left, overlay.top+180, overlay.right, overlay.top+220 };
        drawTextCentered(mem, t1, "Cristal 2048 obtenu !", accentColor, 26, 1);
        RECT t2 = { overlay.left, overlay.top+225, overlay.right, overlay.top+255 };
        drawTextCentered(mem, t2, "ESPACE = continuer   N = nouvelle partie", mutedColor, 13, 0);
    } else if (over) {
        RECT overlay = boardRect;
        fillRoundRect(mem, overlay, RGB(14,25,18), 14);
        RECT t1 = { overlay.left, overlay.top+180, overlay.right, overlay.top+220 };
        drawTextCentered(mem, t1, "Plus aucun mouvement", textColor, 26, 1);
        char buf[64]; sprintf(buf, "Score final : %d - appuyez sur N", score);
        RECT t2 = { overlay.left, overlay.top+225, overlay.right, overlay.top+255 };
        drawTextCentered(mem, t2, buf, mutedColor, 13, 0);
    }

    BitBlt(hdc, 0, 0, client.right, client.bottom, mem, 0, 0, SRCCOPY);
    SelectObject(mem, oldBmp);
    DeleteObject(bmp);
    DeleteDC(mem);
}

LRESULT CALLBACK WndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_CREATE:
            srand((unsigned int)time(NULL));
            resetGame();
            return 0;
        case WM_PAINT: {
            PAINTSTRUCT ps;
            HDC hdc = BeginPaint(hwnd, &ps);
            paint(hwnd, hdc);
            EndPaint(hwnd, &ps);
            return 0;
        }
        case WM_KEYDOWN: {
            int changed = 0;
            if (over) {
                if (wParam == 'N' || wParam == 'R') { resetGame(); changed = 1; }
            } else if (won && !keepPlaying) {
                if (wParam == VK_SPACE) { keepPlaying = 1; changed = 1; }
                else if (wParam == 'N') { resetGame(); changed = 1; }
            } else {
                switch (wParam) {
                    case VK_LEFT:  case 'A': changed = doMove(0); break;
                    case VK_RIGHT: case 'D': changed = doMove(1); break;
                    case VK_UP:    case 'W': changed = doMove(2); break;
                    case VK_DOWN:  case 'S': changed = doMove(3); break;
                    case 'N': resetGame(); changed = 1; break;
                }
            }
            if (changed) InvalidateRect(hwnd, NULL, FALSE);
            return 0;
        }
        case WM_DESTROY:
            PostQuitMessage(0);
            return 0;
    }
    return DefWindowProc(hwnd, msg, wParam, lParam);
}

int WINAPI WinMain(HINSTANCE hInst, HINSTANCE hPrev, LPSTR cmd, int show) {
    WNDCLASSEXA wc = {0};
    wc.cbSize = sizeof(WNDCLASSEXA);
    wc.lpfnWndProc = WndProc;
    wc.hInstance = hInst;
    wc.lpszClassName = "Jeu2048CristauxWindow";
    wc.hCursor = LoadCursor(NULL, IDC_ARROW);
    wc.hbrBackground = CreateSolidBrush(bgColor);
    wc.hIcon = LoadIcon(NULL, IDI_APPLICATION);
    RegisterClassExA(&wc);

    RECT r = {0, 0, WIN_W, WIN_H};
    AdjustWindowRect(&r, WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | WS_MINIMIZEBOX, FALSE);

    HWND hwnd = CreateWindowExA(0, "Jeu2048CristauxWindow", "2048 - Jardin de cristaux",
        WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | WS_MINIMIZEBOX,
        CW_USEDEFAULT, CW_USEDEFAULT, r.right - r.left, r.bottom - r.top,
        NULL, NULL, hInst, NULL);

    ShowWindow(hwnd, show);
    UpdateWindow(hwnd);

    MSG msg;
    while (GetMessage(&msg, NULL, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
    return 0;
}
