Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$AppOut = Join-Path $Root "app\src\main\res\drawable-nodpi"
$WebOut = Join-Path $Root "web\assets"

New-Item -ItemType Directory -Force -Path $AppOut | Out-Null
New-Item -ItemType Directory -Force -Path $WebOut | Out-Null

function ColorA([int]$a, [int]$r, [int]$g, [int]$b) {
    [System.Drawing.Color]::FromArgb($a, $r, $g, $b)
}

function New-Bitmap([int]$w, [int]$h) {
    $bmp = [System.Drawing.Bitmap]::new($w, $h, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $bmp.SetResolution(144, 144)
    return $bmp
}

function New-Graphics($bmp) {
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    return $g
}

function Save-Asset($bmp, [string]$name) {
    $appPath = Join-Path $AppOut $name
    $webPath = Join-Path $WebOut $name
    $bmp.Save($appPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Copy-Item -LiteralPath $appPath -Destination $webPath -Force
}

function Fill-Ellipse($g, $rect, $color) {
    $brush = [System.Drawing.SolidBrush]::new($color)
    $g.FillEllipse($brush, $rect)
    $brush.Dispose()
}

function Stroke-Ellipse($g, $rect, $color, [float]$width) {
    $pen = [System.Drawing.Pen]::new($color, $width)
    $pen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $g.DrawEllipse($pen, $rect)
    $pen.Dispose()
}

function Stroke-Line($g, [float]$x1, [float]$y1, [float]$x2, [float]$y2, $color, [float]$width) {
    $pen = [System.Drawing.Pen]::new($color, $width)
    $pen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $g.DrawLine($pen, $x1, $y1, $x2, $y2)
    $pen.Dispose()
}

function Draw-StarfieldBackground {
    $bmp = New-Bitmap 1080 1920
    $g = New-Graphics $bmp
    $rect = [System.Drawing.Rectangle]::new(0, 0, 1080, 1920)
    $bg = [System.Drawing.Drawing2D.LinearGradientBrush]::new($rect, (ColorA 255 7 11 24), (ColorA 255 13 34 51), 72)
    $g.FillRectangle($bg, $rect)
    $bg.Dispose()

    $rnd = [System.Random]::new(311)
    for ($i = 0; $i -lt 260; $i++) {
        $x = $rnd.Next(0, 1080)
        $y = $rnd.Next(0, 1920)
        $s = $rnd.Next(1, 4)
        $a = $rnd.Next(40, 165)
        Fill-Ellipse $g ([System.Drawing.RectangleF]::new($x, $y, $s, $s)) (ColorA $a 190 248 255)
    }

    $nebulaColors = @(
        @(110, 48, 212, 255, 150, 420, 690),
        @(95, 114, 245, 128, 760, 520, 610),
        @(85, 255, 95, 115, 500, 1040, 780),
        @(80, 255, 215, 90, 220, 1340, 600)
    )
    foreach ($n in $nebulaColors) {
        $path = [System.Drawing.Drawing2D.GraphicsPath]::new()
        $path.AddEllipse([System.Drawing.RectangleF]::new($n[4] - $n[6] / 2, $n[5] - $n[6] / 2, $n[6], $n[6]))
        $brush = [System.Drawing.Drawing2D.PathGradientBrush]::new($path)
        $brush.CenterColor = ColorA $n[0] $n[1] $n[2] $n[3]
        $brush.SurroundColors = [System.Drawing.Color[]]@(ColorA 0 $n[1] $n[2] $n[3])
        $g.FillPath($brush, $path)
        $brush.Dispose()
        $path.Dispose()
    }

    for ($i = -400; $i -lt 1500; $i += 116) {
        Stroke-Line $g $i -40 ($i + 650) 1960 (ColorA 42 98 242 255) 3
        Stroke-Line $g ($i + 46) -40 ($i + 696) 1960 (ColorA 22 255 255 255) 1
    }

    Save-Asset $bmp "bg_nebula_main.png"
    $g.Dispose()
    $bmp.Dispose()
}

function Draw-EnergyRing {
    $bmp = New-Bitmap 1024 1024
    $g = New-Graphics $bmp
    $g.Clear([System.Drawing.Color]::Transparent)
    for ($i = 0; $i -lt 8; $i++) {
        $r = 250 + $i * 28
        $alpha = 34 - $i * 3
        Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(512 - $r, 512 - $r, $r * 2, $r * 2)) (ColorA $alpha 98 242 255) (9 - $i * 0.6)
    }
    $arcPen = [System.Drawing.Pen]::new((ColorA 220 255 215 90), 8)
    $arcPen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $arcPen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $g.DrawArc($arcPen, [System.Drawing.RectangleF]::new(134, 134, 756, 756), 205, 112)
    $g.DrawArc($arcPen, [System.Drawing.RectangleF]::new(180, 180, 664, 664), 12, 72)
    $arcPen.Color = ColorA 230 98 242 255
    $g.DrawArc($arcPen, [System.Drawing.RectangleF]::new(108, 108, 808, 808), 330, 96)
    $arcPen.Dispose()

    $rnd = [System.Random]::new(731)
    for ($i = 0; $i -lt 58; $i++) {
        $angle = $rnd.NextDouble() * [Math]::PI * 2
        $dist = 270 + $rnd.NextDouble() * 160
        $x = 512 + [Math]::Cos($angle) * $dist
        $y = 512 + [Math]::Sin($angle) * $dist
        $s = 4 + $rnd.NextDouble() * 8
        Fill-Ellipse $g ([System.Drawing.RectangleF]::new($x - $s / 2, $y - $s / 2, $s, $s)) (ColorA 185 160 246 255)
    }

    Save-Asset $bmp "fx_energy_ring.png"
    $g.Dispose()
    $bmp.Dispose()
}

function Draw-Planet([string]$name, [int]$stage) {
    $bmp = New-Bitmap 1024 1024
    $g = New-Graphics $bmp
    $g.Clear([System.Drawing.Color]::Transparent)

    $accent = @(
        @(255, 95, 115),
        @(98, 242, 255),
        @(114, 245, 128),
        @(255, 215, 90)
    )[$stage]
    $base1 = @(
        @(52, 56, 70),
        @(32, 82, 126),
        @(32, 116, 104),
        @(62, 174, 136)
    )[$stage]
    $base2 = @(
        @(20, 24, 36),
        @(23, 42, 82),
        @(23, 70, 74),
        @(22, 104, 105)
    )[$stage]

    for ($i = 0; $i -lt 6; $i++) {
        $r = 410 + $i * 34
        Fill-Ellipse $g ([System.Drawing.RectangleF]::new(512 - $r, 512 - $r, $r * 2, $r * 2)) (ColorA (28 - $i * 4) $accent[0] $accent[1] $accent[2])
    }

    $planetPath = [System.Drawing.Drawing2D.GraphicsPath]::new()
    $planetPath.AddEllipse([System.Drawing.RectangleF]::new(132, 132, 760, 760))
    $brush = [System.Drawing.Drawing2D.PathGradientBrush]::new($planetPath)
    $brush.CenterPoint = [System.Drawing.PointF]::new(410, 330)
    $brush.CenterColor = ColorA 255 ([Math]::Min(255, $base1[0] + 52)) ([Math]::Min(255, $base1[1] + 52)) ([Math]::Min(255, $base1[2] + 52))
    $brush.SurroundColors = [System.Drawing.Color[]]@(ColorA 255 $base2[0] $base2[1] $base2[2])
    $g.FillPath($brush, $planetPath)
    $brush.Dispose()

    $oldClip = $g.Clip
    $g.SetClip($planetPath)
    $rnd = [System.Random]::new(100 + $stage)
    for ($i = 0; $i -lt (8 + $stage * 3); $i++) {
        $x = 170 + $rnd.Next(0, 660)
        $y = 170 + $rnd.Next(0, 660)
        $w = 120 + $rnd.Next(0, 220)
        $h = 42 + $rnd.Next(0, 130)
        $a = 72 + $stage * 24
        $c = if ($stage -lt 2) { ColorA $a 98 242 255 } else { ColorA $a 114 245 128 }
        Fill-Ellipse $g ([System.Drawing.RectangleF]::new($x - $w / 2, $y - $h / 2, $w, $h)) $c
    }

    if ($stage -eq 0) {
        for ($i = 0; $i -lt 12; $i++) {
            $a = $i * 0.55
            $x1 = 512 + [Math]::Cos($a) * (80 + $rnd.Next(0, 70))
            $y1 = 512 + [Math]::Sin($a) * (80 + $rnd.Next(0, 70))
            $x2 = 512 + [Math]::Cos($a + 0.22) * (260 + $rnd.Next(0, 220))
            $y2 = 512 + [Math]::Sin($a + 0.18) * (260 + $rnd.Next(0, 220))
            Stroke-Line $g $x1 $y1 $x2 $y2 (ColorA 230 255 95 115) 11
            Stroke-Line $g $x1 $y1 $x2 $y2 (ColorA 180 255 215 90) 3
        }
    } elseif ($stage -eq 1) {
        Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(344, 344, 336, 336)) (ColorA 220 98 242 255) 10
        Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(272, 272, 480, 480)) (ColorA 120 255 215 90) 5
    } elseif ($stage -eq 2) {
        for ($i = 0; $i -lt 14; $i++) {
            $x = 220 + $rnd.Next(0, 580)
            $y = 230 + $rnd.Next(0, 560)
            Fill-Ellipse $g ([System.Drawing.RectangleF]::new($x, $y, 24 + $rnd.Next(0, 34), 24 + $rnd.Next(0, 34))) (ColorA 210 114 245 128)
        }
    } else {
        for ($i = 0; $i -lt 24; $i++) {
            $x = 180 + $rnd.Next(0, 660)
            $y = 180 + $rnd.Next(0, 660)
            Fill-Ellipse $g ([System.Drawing.RectangleF]::new($x, $y, 18 + $rnd.Next(0, 26), 18 + $rnd.Next(0, 26))) (ColorA 220 255 215 90)
        }
        Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(314, 314, 396, 396)) (ColorA 190 255 255 255) 5
    }
    $g.Clip = $oldClip

    Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(132, 132, 760, 760)) (ColorA 250 255 255 255) 9
    Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(112, 152, 800, 720)) (ColorA 205 $accent[0] $accent[1] $accent[2]) 7
    Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(86, 234, 852, 556)) (ColorA 210 255 215 90) 4

    Save-Asset $bmp $name
    $g.Dispose()
    $bmp.Dispose()
}

function Draw-AI([string]$name, [int[]]$accent, [int[]]$secondary) {
    $bmp = New-Bitmap 512 512
    $g = New-Graphics $bmp
    $g.Clear([System.Drawing.Color]::Transparent)
    for ($i = 0; $i -lt 5; $i++) {
        $r = 180 + $i * 28
        Fill-Ellipse $g ([System.Drawing.RectangleF]::new(256 - $r, 256 - $r, $r * 2, $r * 2)) (ColorA (42 - $i * 6) $accent[0] $accent[1] $accent[2])
    }
    Fill-Ellipse $g ([System.Drawing.RectangleF]::new(112, 90, 288, 300)) (ColorA 255 18 28 48)
    Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(112, 90, 288, 300)) (ColorA 245 $accent[0] $accent[1] $accent[2]) 8
    Stroke-Ellipse $g ([System.Drawing.RectangleF]::new(156, 128, 200, 214)) (ColorA 230 255 255 255) 5
    Fill-Ellipse $g ([System.Drawing.RectangleF]::new(178, 154, 156, 156)) (ColorA 245 $accent[0] $accent[1] $accent[2])
    Fill-Ellipse $g ([System.Drawing.RectangleF]::new(218, 194, 76, 76)) (ColorA 230 $secondary[0] $secondary[1] $secondary[2])
    Stroke-Line $g 160 402 352 402 (ColorA 220 $accent[0] $accent[1] $accent[2]) 10
    Stroke-Line $g 204 432 308 432 (ColorA 170 255 255 255) 6
    Save-Asset $bmp $name
    $g.Dispose()
    $bmp.Dispose()
}

Draw-StarfieldBackground
Draw-EnergyRing
Draw-Planet "planet_collapsed.png" 0
Draw-Planet "planet_activated.png" 1
Draw-Planet "planet_growth.png" 2
Draw-Planet "planet_complete.png" 3
Draw-AI "ai_companion_life.png" @(114, 245, 128) @(255, 215, 90)
Draw-AI "ai_companion_industrial.png" @(98, 242, 255) @(120, 170, 255)
Draw-AI "ai_companion_combat.png" @(255, 95, 115) @(255, 215, 90)

Write-Host "Generated main visual assets into $AppOut and $WebOut"
