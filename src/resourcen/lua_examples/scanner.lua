num = strip.ledNumber() -- get the amount of leds
c = {} -- this is our color table
led = 0
direction = 1
while true do
	if strip.delayReached() then
		led = led + direction
		if led <= 0 then
			led = 0
			direction = 1
		end
		if led >= num then
			led = num
			direction = -1
		end
		for i=1,num do
			c[i] = {0,0,0}
		end
		c[led] = {255, 0, 0}
		strip.show(c)
	end
end