num = strip.ledNumber()
c = {}
rainbow = {}
stepping = 0

function calculateColor(st)
	local c = {}
	c.r = (127 * math.sin(math.pi * st) + 127)
	c.g = (127 * math.sin(math.pi * st - (math.pi / 2)) + 127)
	c.b = (127 * math.sin(math.pi * st - math.pi) + 127)
	return c
end

-- init rainbow table
index = 0
while stepping < 2 do
	rainbow[index] = calculateColor(stepping)
	stepping = stepping + 0.01
	index = index + 1
end

rainStep = 0
-- fill strip
for i = num, 1, -1 do
	color = rainbow[rainStep]
	c[i] = {color.r,color.g,color.b}
	rainStep = rainStep + 1
	if rainStep == index then
		rainStep = 0
	end
end
strip.show(c)

while true do
	if strip.delayReached() then
		strip.shiftRight(1)
		color = rainbow[rainStep]
		strip.setPixel(1, {color.r,color.g,color.b})
		rainStep = rainStep + 1
		if rainStep == index then
			rainStep = 0
		end
	end
end