//
// Created by fenge on 2024/8/31.
//

#ifndef BMWRIDER_HOOK_H
#define BMWRIDER_HOOK_H

#endif //BMWRIDER_HOOK_H
#include <windows.h>
void HookFunction64(char* lpModule, LPCSTR lpFuncName, LPVOID lpFunction);
void UnHookFunction64(char* lpModule, LPCSTR lpFuncName);
void HookFunctionAdress64(LPVOID FuncAddress, LPVOID lpFunction);
void UnHookFunctionAdress64(LPVOID FuncAddress);