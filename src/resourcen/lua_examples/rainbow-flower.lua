brStep = 0.0
brightness = 0
hue = 30

function calculateBrightness(x)
	local br = (50 * math.cos(math.pi * (x-1)) + 50)
	return br
end

while true do
	if strip.delayReached() then
		strip.shiftRight(1)
		brightness = calculateBrightness(brStep)
		color = colorUtil.hsv(hue, 100, brightness)
		
		strip.setPixel(1, color)
		--print("Color:", color[1], color[2], color[3], "Hue:", hue, "Brightness:", brightness)
		
		brStep = brStep + 0.2
		if brStep > 2 then
			brStep = 0.2
			hue = hue + 60
		end
		
		if(brStep > 1) then
			hue = hue - 6
		else
			hue = hue + 6;
		end
		
		if hue > 360 then
			hue = hue - 360
		end
		if hue < 0 then
			hue = 360 + hue
		end
	end
end