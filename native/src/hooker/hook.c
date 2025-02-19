//
// Created by fenge on 2024/8/31.
//

#include "hook.h"
#include <windows.h>
extern BYTE OldCode[12] = { 0x00 };
extern BYTE HookCode[12] = { 0x48, 0xB8, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0xFF, 0xE0 };

void HookFunction64(char* lpModule, LPCSTR lpFuncName, LPVOID lpFunction)
{
    DWORD_PTR FuncAddress = (UINT64)GetProcAddress(GetModuleHandle(lpModule), lpFuncName);
    DWORD OldProtect = 0;
    //MessageBoxA(NULL,"1","Fish",0);
    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        //MessageBoxA(NULL,"2","Fish",0);

        memcpy(OldCode, (LPVOID)FuncAddress, 12);                   // 拷贝原始机器码指令
        *(PINT64)(HookCode + 2) = (UINT64)lpFunction;               // 填充90为指定跳转地址
    }
    memcpy((LPVOID)FuncAddress, &HookCode, sizeof(HookCode));
    //MessageBoxA(NULL,"3","Fish",0);
    // 拷贝Hook机器指令
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}
void UnHookFunction64(char* lpModule, LPCSTR lpFuncName)
{
    DWORD OldProtect = 0;
    UINT64 FuncAddress = (UINT64)GetProcAddress(GetModuleHandleA(lpModule), lpFuncName);
    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        memcpy((LPVOID)FuncAddress, OldCode, sizeof(OldCode));
    }
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}
void HookFunctionAdress64(LPVOID FuncAddress, LPVOID lpFunction)
{
    DWORD OldProtect = 0;
    //MessageBoxA(NULL,"1","Fish",0);

    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        //MessageBoxA(NULL,"2","Fish",0);

        memcpy(OldCode, (LPVOID)FuncAddress, 12);                   // 拷贝原始机器码指令
        *(PINT64)(HookCode + 2) = (UINT64)lpFunction;               // 填充90为指定跳转地址
    }
    memcpy((LPVOID)FuncAddress, &HookCode, sizeof(HookCode));
    //MessageBoxA(NULL,"3","Fish",0);
    // 拷贝Hook机器指令
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}
void UnHookFunctionAdress64(LPVOID FuncAddress)
{
    DWORD OldProtect = 0;
    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        memcpy((LPVOID)FuncAddress, OldCode, sizeof(OldCode));
    }
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}