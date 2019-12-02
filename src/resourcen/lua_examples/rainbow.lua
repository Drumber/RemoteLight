num = strip.ledNumber()
c = {}
stepping = 0

function calculateColor(st)
	local c = {}
	c.r = (127 * math.sin(math.pi * st) + 127)
	c.g = (127 * math.sin(math.pi * st - (math.pi / 2)) + 127)
	c.b = (127 * math.sin(math.pi * st - math.pi) + 127)
	return c
end
	
while true do
	if strip.delayReached() then
		color = calculateColor(stepping)
		for i=1,num do
			c[i] = {color.r,color.g,color.b}
		end
		strip.show(c)
		stepping = stepping + 0.01
		if stepping == 2 then
			stepping = 0
		end
	end
end