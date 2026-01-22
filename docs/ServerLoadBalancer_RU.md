# ServerLoadBalancer - Система адаптивного пропуска тиков

## Описание

ServerLoadBalancer - это система оптимизации производительности, которая автоматически замедляет работу механизмов Industrial Foregoing при падении TPS сервера. Это позволяет снизить нагрузку на сервер во время лагов, сохраняя при этом общую производительность механизмов за счёт компенсации пропущенных тиков.

## Основные возможности

- **Адаптивный пропуск тиков**: Механизмы автоматически пропускают тики при низком TPS
- **Компенсация**: Пропущенные тики компенсируются увеличенным выходом/ростом
- **Два режима работы**: Глобальный TPS или TPS для каждого мира отдельно
- **Плавное изменение**: Постепенное увеличение/уменьшение интервала пропуска
- **Централизованная настройка**: Все параметры в одном месте (настройки из конфигов отдельных машин удалены)

## Конфигурация

Файл конфигурации: `config/industrialforegoing/server.toml`

Секция: `[ServerConfig.ServerLoadBalancerConfig]`

> **Важно**: Начиная с версии 3.6.42, настройки tick skipping удалены из `HydroponicBedConfig` и `SimulatedHydroponicBedConfig`. Все настройки теперь централизованы в секции ServerLoadBalancer.

### Основные параметры

| Параметр | По умолчанию | Описание |
|----------|--------------|----------|
| `enabled` | true | Главный переключатель системы. При отключении все механизмы работают на полной скорости |
| `perWorldTps` | false | Режим отслеживания TPS: false = глобальный, true = для каждого мира отдельно |

### Параметры TPS

| Параметр | По умолчанию | Описание |
|----------|--------------|----------|
| `tpsSampleInterval` | 20 | Интервал замеров TPS в тиках (20 = 1 секунда) |
| `normalTps` | 19.0 | Порог TPS для нормальной работы (без пропуска) |
| `highLoadTps` | 15.0 | Порог TPS для средней нагрузки (пропуск каждого 2-го тика) |
| `criticalLoadTps` | 10.0 | Порог TPS для высокой нагрузки (пропуск каждого 4-го тика) |

### Параметры пропуска тиков

| Параметр | По умолчанию | Описание |
|----------|--------------|----------|
| `minSkippedTicks` | 1 | Минимальный интервал пропуска (1 = без пропуска) |
| `maxSkippedTicks` | 8 | Максимальный интервал пропуска |
| `criticalGradualProgressiveSkippedTicks` | true | Плавное увеличение пропуска при росте нагрузки |
| `nonCriticalGradualRegressiveSkippedTicks` | true | Плавное уменьшение пропуска при снижении нагрузки |

## Режим Per-World TPS

### Когда использовать

Режим `perWorldTps = true` идеально подходит для серверов, где:
- У каждого игрока свой отдельный мир (skyblock, острова)
- Миры изолированы друг от друга
- Лаги в одном мире не должны влиять на другие

### Как это работает

1. **Глобальный режим** (`perWorldTps = false`):
   - Отслеживается общий TPS сервера
   - При падении TPS все механизмы во всех мирах замедляются одинаково

2. **Режим по мирам** (`perWorldTps = true`):
   - TPS отслеживается для каждого измерения отдельно
   - Механизмы замедляются только в тех мирах, где низкий TPS
   - Миры с нормальным TPS продолжают работать на полной скорости

### Пример

Сервер skyblock с 10 игроками:
- Игрок A строит большую ферму → его мир лагает (TPS 12)
- Остальные 9 игроков играют нормально → их миры работают на TPS 20

**С `perWorldTps = false`**: Все 10 игроков получат замедление механизмов

**С `perWorldTps = true`**: Только игрок A получит замедление, остальные работают нормально

## Поддерживаемые механизмы

- Hydroponic Bed (Гидропонная грядка)
- Simulated Hydroponic Bed (Симулированная гидропонная грядка)

## Логика работы

### Ступенчатый режим (без плавного изменения)

```
TPS >= 19.0  → Интервал пропуска = 1 (каждый тик)
TPS >= 15.0  → Интервал пропуска = 2 (каждый 2-й тик)
TPS >= 10.0  → Интервал пропуска = 4 (каждый 4-й тик)
TPS < 10.0   → Интервал пропуска = 8 (каждый 8-й тик)
```

### Плавный режим (с градиентным изменением)

При включённых параметрах `criticalGradualProgressiveSkippedTicks` или `nonCriticalGradualRegressiveSkippedTicks`, интервал пропуска вычисляется линейной интерполяцией между `minSkippedTicks` и `maxSkippedTicks` на основе текущего TPS.

## Компенсация пропущенных тиков

Система автоматически компенсирует пропущенные тики:
- При пропуске каждого 2-го тика → рост/выход умножается на 2
- При пропуске каждого 4-го тика → рост/выход умножается на 4
- И так далее

Это гарантирует, что общая производительность механизмов остаётся неизменной.

## Экономия ресурсов

| Интервал пропуска | Экономия CPU |
|-------------------|--------------|
| 2 (каждый 2-й тик) | 50% |
| 4 (каждый 4-й тик) | 75% |
| 8 (каждый 8-й тик) | 87.5% |

## Рекомендуемые настройки

### Для одиночного сервера
```toml
[ServerConfig.ServerLoadBalancerConfig]
enabled = true
perWorldTps = false
```

### Для skyblock/островного сервера
```toml
[ServerConfig.ServerLoadBalancerConfig]
enabled = true
perWorldTps = true
```

### Для сервера с критическими лагами
```toml
[ServerConfig.ServerLoadBalancerConfig]
enabled = true
normalTps = 18.0
highLoadTps = 14.0
criticalLoadTps = 8.0
maxSkippedTicks = 16
```

### Агрессивная оптимизация (пример от пользователя)
```toml
[ServerConfig.ServerLoadBalancerConfig]
enabled = true
perWorldTps = true
tpsSampleInterval = 10
normalTps = 20.0
highLoadTps = 17.0
criticalLoadTps = 14.0
minSkippedTicks = 3
maxSkippedTicks = 10
```

### Полное отключение системы
```toml
[ServerConfig.ServerLoadBalancerConfig]
enabled = false
```

## API для разработчиков

```java
// Получить интервал пропуска для конкретного мира
int skipInterval = ServerLoadBalancer.getTickSkipInterval(level);

// Получить текущий TPS мира
double tps = ServerLoadBalancer.getCurrentTPS(level);

// Проверить, активен ли пропуск тиков
boolean isSkipping = ServerLoadBalancer.isSkippingActive(level);

// Использование с кастомными порогами
int skipInterval = ServerLoadBalancer.getTickSkipInterval(
    level,
    true,  // enabled
    15,    // highLoadThreshold
    10,    // criticalLoadThreshold
    8      // maxSkip
);
```

## Логирование

При старте сервера в консоль выводится информация о конфигурации:

```
[ServerLoadBalancer] Initialized. Enabled: true, Mode: PER_WORLD, Skip: 1-8, TPS thresholds: 19.0/15.0/10.0
```

## Миграция с предыдущих версий

Если вы обновляетесь с версии до 3.6.42, удалите старые настройки из конфигов машин:

**Удалите из `machine-resource-production.toml`:**
- `adaptiveTickSkipping`
- `highLoadThresholdTPS`
- `criticalLoadThresholdTPS`
- `maxTickSkip`

**Добавьте в `server.toml` секцию `[ServerConfig.ServerLoadBalancerConfig]`** с нужными настройками.
