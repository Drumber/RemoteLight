num = strip.ledNumber() -- get the amount of leds
c = {} -- this is our color table
led = 0
color = strip.randomColor()
while true do
	if strip.delayReached() then
		led = led + 1
		if led > num then
			led = 1
			color = strip.randomColor() -- get a random color
		end
		strip.setPixel(led, color)
	end
end