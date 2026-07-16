@echo off
cd /d C:\workspace\yipei-elderly-care
echo ===== Deleting conflicting files =====
del /f "src\main\java\com\yipei\entity\ApiResponse.java" 2>nul
del /f "src\main\java\com\yipei\entity\UpdateUserInfoRequest.java" 2>nul
del /f "src\main\java\com\yipei\exception\ForbiddenException.java" 2>nul
del /f "src\main\java\com\yipei\exception\NotFoundException.java" 2>nul
echo ===== Running git pull =====
git pull
echo ===== Final HEAD =====
git log --oneline -1
echo ===== Git Status =====
git status --short